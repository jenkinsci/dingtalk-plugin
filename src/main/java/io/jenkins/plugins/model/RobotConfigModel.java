package io.jenkins.plugins.model;

import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.DingTalkSecurityPolicyConfig;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * 机器人配置信息
 *
 * @author liuwei
 * @date 2019/12/23 14:08
 */
@Data
@Slf4j
public class RobotConfigModel {

  /** 关键字 */
  private String keys;

  /** 签名 */
  private String sign;

  /** api 接口 */
  private String webhook;

  public String getServer() {
    if (StringUtils.isEmpty(sign)) {
      return webhook;
    }
    long timestamp = System.currentTimeMillis();
    return webhook + "&timestamp=" + timestamp + "&sign=" + createSign(timestamp, sign);
  }

  /**
   * 从机器人配置中解析元信息
   *
   * @param robotConfig 配置
   * @return 机器人配置
   */
  public static RobotConfigModel of(DingTalkRobotConfig robotConfig) {
    ArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs =
        robotConfig.getSecurityPolicyConfigs();
    RobotConfigModel meta = new RobotConfigModel();
    meta.setWebhook(robotConfig.getWebhook());
    // 解析安全策略
    securityPolicyConfigs.forEach(
        config -> {
          if (StringUtils.isEmpty(config.getValue())) {
            return;
          }
          String type = config.getType();
          SecurityPolicyEnum securityPolicyEnum = SecurityPolicyEnum.valueOf(type);
          switch (securityPolicyEnum) {
            case KEY:
              meta.setKeys(config.getValue());
              break;
            case SECRET:
              meta.setSign(config.getValue());
              break;
            default:
              log.error("对应的安全策略不存在：" + type);
          }
        });
    return meta;
  }

  /**
   * 签名方法
   *
   * @return 签名
   */
  private static String createSign(long timestamp, String secret) {
    String result = "";
    try {
      String seed = timestamp + "\n" + secret;
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] signData = mac.doFinal(seed.getBytes(StandardCharsets.UTF_8));
      result =
          URLEncoder.encode(
              new String(Base64.encodeBase64(signData), StandardCharsets.UTF_8.name()),
              StandardCharsets.UTF_8.name());
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
      log.error("钉钉插件设置签名失败：", e);
    }
    return result;
  }
}
