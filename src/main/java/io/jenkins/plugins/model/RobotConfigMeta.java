package io.jenkins.plugins.model;

import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.DingTalkSecurityPolicyConfig;
import io.jenkins.plugins.enums.SecurityPolicyType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.binary.Base64;

/**
 * @author liuwei
 * @date 2019/12/23 14:08
 * @desc 安全策略元信息
 */
@Data
@Builder
@Log4j
public class RobotConfigMeta {

  /**
   * 关键字
   */
  private String keys;

  /**
   * 白名单 ip
   */
  private Set<String> ips;

  /**
   * 请求地址
   */
  private String server;

  /**
   * 签名方法
   *
   * @return String
   */
  private static String createSign(long timestamp, String secret) {
    String result = "";
    try {
      String stringToSign = timestamp + "\n" + secret;
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
      result = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
      log.error(e);
    }
    return result;
  }

  /**
   * 从机器人配置中解析元信息
   *
   * @param robotConfig 配置
   * @return RobotConfigMeta
   */
  public static RobotConfigMeta of(DingTalkRobotConfig robotConfig) {
    CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs = robotConfig
        .getSecurityPolicyConfigs();
    String webhook = robotConfig.getWebhook();
    RobotConfigMetaBuilder builder = RobotConfigMeta.builder();
    builder.server(webhook);
    // 解析安全策略
    securityPolicyConfigs.forEach(config -> {
      if (!config.isChecked()) {
        return;
      }
      String type = config.getType();
      long timestamp = System.currentTimeMillis();
      SecurityPolicyType securityPolicyType = SecurityPolicyType.valueOf(type);
      switch (securityPolicyType) {
        case KEY:
          builder.keys(config.getValue());
          break;
        case SECRET:
          builder.server(
              webhook +
                  "&timestamp=" + timestamp +
                  "&sign=" + RobotConfigMeta.createSign(timestamp, config.getValue())
          );
          break;
        case IP:
          builder.ips(config.getValues());
          break;
        default:
          log.error("对应的安全策略不存在：" + type);
      }
    });
    return builder.build();
  }

}
