package io.jenkins.plugins.sdk;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DingTalkRobotResponse <a href="https://open.dingtalk.com/document/orgapp/custom-robots-send-group-messages#h2-zl3-3lb-eww">...</a>
 */
@Data
@NoArgsConstructor
public class DingTalkRobotResponse {
    private int errcode;
    private String errmsg;
}
