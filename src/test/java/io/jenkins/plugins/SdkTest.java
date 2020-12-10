package io.jenkins.plugins;

import hudson.util.Secret;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.DingTalkSender;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author liuwei
 * @date 2020/3/28 17:40
 */
public class SdkTest {

  public static void main(String... args) {
    CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs =
        new CopyOnWriteArrayList<>();
    securityPolicyConfigs.add(new DingTalkSecurityPolicyConfig("KEY", "jenkins", ""));
    DingTalkRobotConfig robot = new DingTalkRobotConfig();
    robot.setWebhook(
        Secret.fromString(
            "https://oapi.dingtalk.com/robot/send?access_token=af0f18ea706e6205c59ecc32011f3a2b5a87da8f565abf3e9033043d496438f9"));
    robot.setSecurityPolicyConfigs(securityPolicyConfigs);

    DingTalkSender sender = new DingTalkSender(robot);
    String text =
        BuildJobModel.builder()
            .projectName("欢迎使用钉钉机器人插件~")
            .projectUrl("/")
            .jobName("系统配置")
            .jobUrl("/configure")
            .statusType(BuildStatusEnum.SUCCESS)
            .duration("-")
            .executorName("test")
            .executorMobile("test")
            .build()
            .toMarkdown();
    MessageModel msg =
        MessageModel.builder()
            .title("钉钉机器人测试成功")
            .text(text)
            .messageUrl("/")
            .atAll(false)
            .build();
    sender.sendText(msg, null);
  }
}
