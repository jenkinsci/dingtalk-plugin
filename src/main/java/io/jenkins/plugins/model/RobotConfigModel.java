package io.jenkins.plugins.model;

import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.DingTalkSecurityPolicyConfig;
import io.jenkins.plugins.enums.SecurityPolicyEnum;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Robot configuration information
 *
 * @author liuwei
 */
@Data
@Slf4j
public class RobotConfigModel {

    /**
     * Keywords
     */
    private String keys;

    /**
     * Signature
     */
    private String sign;

    /**
     * API endpoint
     */
    private String webhook;

    public String getServer() {
        if (StringUtils.isEmpty(sign)) {
            return webhook;
        }
        long timestamp = System.currentTimeMillis();
        return webhook + "&timestamp=" + timestamp + "&sign=" + createSign(timestamp, sign);
    }

    /**
     * Parse metadata from robot configuration
     *
     * @param robotConfig configuration
     * @return robot configuration
     */
    public static RobotConfigModel of(DingTalkRobotConfig robotConfig) {
        ArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs =
                robotConfig.getSecurityPolicyConfigs();
        RobotConfigModel meta = new RobotConfigModel();
        meta.setWebhook(robotConfig.getWebhook());
        // Parse security policies
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
                            log.error("Corresponding security policy does not exist: {}", type);
                    }
                });
        return meta;
    }

    /**
     * Signature method
     * <a href="https://open.dingtalk.com/document/orgapp/customize-robot-security-settings#title-7fs-kgs-36x">...</a>
     *
     * @return signature
     */
    static String createSign(long timestamp, String secret) {
        String stringToSign = timestamp + "\n" + secret;

        byte[] hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmac(stringToSign);
        return URLEncoder.encode(Base64.encodeBase64String(hmac), StandardCharsets.UTF_8);
    }
}
