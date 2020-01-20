package io.jenkins.plugins.model;

import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.DingTalkSecurityPolicyConfig;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.binary.Base64;

/**
 * @author liuwei
 * @date 2019/12/23 14:08
 * @desc 机器人配置信息
 */
@Data
@Log4j
public class RobotConfigModel {

  /**
   * 关键字
   */
  private String keys;

  /**
   * 白名单 ip
   */
  private Set<String> ips;

  /**
   * 签名
   */
  private String sign;

  /**
   * api 接口
   */
  private String webhook;

  /**
   * 请求地址
   */
  private String server;


  public String getServer() {
    long timestamp = System.currentTimeMillis();
    return webhook +
        "&timestamp=" + timestamp +
        "&sign=" + RobotConfigModel.createSign(timestamp, sign);
  }

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
  public static RobotConfigModel of(DingTalkRobotConfig robotConfig) {
    CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs = robotConfig
        .getSecurityPolicyConfigs();
    RobotConfigModel meta = new RobotConfigModel();
    meta.setWebhook(robotConfig.getWebhook());
    // 解析安全策略
    securityPolicyConfigs.forEach(config -> {
      if (!config.isChecked()) {
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
        case IP:
          meta.setIps(config.getValues());
          break;
        default:
          log.error("对应的安全策略不存在：" + type);
      }
    });
    return meta;
  }

}
