package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jenkins.plugins.enums.SecurityPolicyEnum;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DingTalkRobotConfigTest {

  @Test
  void getSecurityPolicyConfigsConvertsTypedEntries() {
    ArrayList<DingTalkSecurityPolicyConfig> configs = new ArrayList<>();
    DingTalkSecurityPolicyConfig keyConfig =
        new DingTalkSecurityPolicyConfig(SecurityPolicyEnum.KEY.name(), "keyword", "desc");
    DingTalkSecurityPolicyConfig secretConfig =
        new DingTalkSecurityPolicyConfig(SecurityPolicyEnum.SECRET.name(), "secret", "desc");
    DingTalkSecurityPolicyConfig otherConfig =
        new DingTalkSecurityPolicyConfig("OTHER", "other", "desc");
    configs.add(keyConfig);
    configs.add(secretConfig);
    configs.add(otherConfig);

    DingTalkRobotConfig robot =
        new DingTalkRobotConfig(
            "id",
            "name",
            "https://oapi.dingtalk.com/robot/send?access_token=token",
            configs);

    List<DingTalkSecurityPolicyConfig> resolved = robot.getSecurityPolicyConfigs();

    assertEquals(3, resolved.size());
    assertTrue(resolved.get(0) instanceof KeySecurityPolicyConfig);
    assertTrue(resolved.get(1) instanceof SecretSecurityPolicyConfig);
    assertSame(otherConfig, resolved.get(2));
    assertEquals(SecurityPolicyEnum.KEY.name(), resolved.get(0).getType());
    assertEquals("keyword", resolved.get(0).getValue());
    assertEquals(SecurityPolicyEnum.SECRET.name(), resolved.get(1).getType());
    assertEquals("secret", resolved.get(1).getValue());
  }

  @Test
  void getSecurityPolicyConfigsPreservesTypedInstances() {
    KeySecurityPolicyConfig keyConfig = new KeySecurityPolicyConfig("keyword");
    SecretSecurityPolicyConfig secretConfig = new SecretSecurityPolicyConfig("secret");
    ArrayList<DingTalkSecurityPolicyConfig> configs = new ArrayList<>();
    configs.add(keyConfig);
    configs.add(secretConfig);

    DingTalkRobotConfig robot =
        new DingTalkRobotConfig(
            "id",
            "name",
            "https://oapi.dingtalk.com/robot/send?access_token=token",
            configs);

    List<DingTalkSecurityPolicyConfig> resolved = robot.getSecurityPolicyConfigs();

    assertEquals(2, resolved.size());
    assertSame(keyConfig, resolved.get(0));
    assertSame(secretConfig, resolved.get(1));
  }

  @Test
  void getSecurityPolicyConfigsHandlesNullList() {
    DingTalkRobotConfig robot =
        new DingTalkRobotConfig(
            "id",
            "name",
            "https://oapi.dingtalk.com/robot/send?access_token=token",
            null);

    List<DingTalkSecurityPolicyConfig> resolved = robot.getSecurityPolicyConfigs();

    assertNotNull(resolved);
    assertTrue(resolved.isEmpty());
  }

  @Test
  void getSecurityPolicyConfigsSkipsBlankValues() {
    ArrayList<DingTalkSecurityPolicyConfig> configs = new ArrayList<>();
    configs.add(new DingTalkSecurityPolicyConfig(SecurityPolicyEnum.KEY.name(), "", "desc"));
    configs.add(new DingTalkSecurityPolicyConfig(SecurityPolicyEnum.SECRET.name(), " ", "desc"));
    configs.add(new DingTalkSecurityPolicyConfig(SecurityPolicyEnum.SECRET.name(), "secret", "desc"));

    DingTalkRobotConfig robot =
        new DingTalkRobotConfig(
            "id",
            "name",
            "https://oapi.dingtalk.com/robot/send?access_token=token",
            configs);

    List<DingTalkSecurityPolicyConfig> resolved = robot.getSecurityPolicyConfigs();

    assertEquals(1, resolved.size());
    assertTrue(resolved.get(0) instanceof SecretSecurityPolicyConfig);
    assertEquals("secret", resolved.get(0).getValue());
  }
}
