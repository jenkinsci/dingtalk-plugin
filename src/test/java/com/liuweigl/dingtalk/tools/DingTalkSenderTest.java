package com.liuweigl.dingtalk.tools;

import com.liuweigl.dingtalk.DingTalkRobotConfig;
import com.liuweigl.dingtalk.DingTalkSecurityPolicyConfig;
import com.liuweigl.dingtalk.enums.BuildStatusType;
import com.liuweigl.dingtalk.enums.SecurityPolicyType;
import com.liuweigl.dingtalk.model.BuildMessage;
import com.taobao.api.ApiException;
import java.util.concurrent.CopyOnWriteArrayList;

public class DingTalkSenderTest {

  public static void main(String[] args) throws ApiException {
    DingTalkRobotConfig robotConfig = new DingTalkRobotConfig();
    CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs = new CopyOnWriteArrayList<>();
    DingTalkSecurityPolicyConfig securityPolicyConfig = new DingTalkSecurityPolicyConfig(
        true,
        SecurityPolicyType.SECRET.name(),
        "SECcf8e25bb7a143b5baa9f935b5295dcd6cbaf796af2f69231bb21ba9d4bdbc32b",
        ""
    );
    securityPolicyConfigs.add(securityPolicyConfig);
    robotConfig.setWebhook(
        "https://oapi.dingtalk.com/robot/send?access_token=d3e797903a606e7a19f510dc22863b87112e6783556bdbd264e9267d445b4e81");
    robotConfig.setSecurityPolicyConfigs(securityPolicyConfigs);
    DingTalkSender sender = new DingTalkSender(robotConfig);
    BuildMessage message = BuildMessage.builder()
        .projectName("test")
        .projectUrl("http://127.0.0.1:8080/jenkins/job/test")
        .jobName("#37")
        .jobUrl("http://127.0.0.1:8080/jenkins/job/test/37/")
        .statusType(BuildStatusType.FAILURE)
        .duration("4 分 15 秒")
        .executorName("刘炜")
        .executorMobile("18516600940")
        .datetime("2019/12/16 17:31:12")
        .build();
    sender.send(message);

  }
}
