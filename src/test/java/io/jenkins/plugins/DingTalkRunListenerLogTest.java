package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Covers the "发送的消息详情" line of the build log, which is the only place a user can see what the
 * plugin actually posted to DingTalk.
 */
@WithJenkins
class DingTalkRunListenerLogTest {

  /** A robot pointing at a closed port: sending fails, which the listener logs and moves on. */
  private static final String UNREACHABLE = "http://127.0.0.1:1/robot/send?access_token=none";

  private static final String ROBOT_ID = "robot-under-test";

  /**
   * Expanded by {@code DingTalkRunListener} into the project name, so finding it in the log proves
   * the logged object is the expanded payload rather than the raw template.
   */
  private static final String TEMPLATE = "payload-marker ${PROJECT_NAME}";

  private static void registerRobot(JenkinsRule r) {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig(ROBOT_ID, "robot under test", UNREACHABLE, new ArrayList<>()));
    DingTalkGlobalConfig config = DingTalkGlobalConfig.getInstance();
    config.setRobotConfigs(robots);
    // Every DingTalkUtils.log call is gated on this, so the diagnostic lines only reach the build
    // log once a user turns verbose logging on — which is exactly when they are diagnosing.
    config.setVerbose(true);
  }

  private static FreeStyleProject jobNotifying(JenkinsRule r, String name, boolean raw)
      throws Exception {
    DingTalkNotifierConfig notifier = new DingTalkNotifierConfig(
        raw,
        false,
        true,
        ROBOT_ID,
        "robot under test",
        false,
        null,
        raw ? null : TEMPLATE,
        raw ? TEMPLATE : null,
        // Not Set.of(): JEP-200 class filtering allows ImmutableCollections$List12/ListN but has
        // no Set or Map counterpart, so saving the job config would fail.
        new HashSet<>(List.of(NoticeOccasionEnum.SUCCESS.name())));
    ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
    notifiers.add(notifier);

    FreeStyleProject job = r.createFreeStyleProject(name);
    job.addProperty(new DingTalkJobProperty(notifiers));
    return job;
  }

  @Test
  void logsTheBuiltInPayloadInsteadOfNull(JenkinsRule r) throws Exception {
    registerRobot(r);
    FreeStyleProject job = jobNotifying(r, "built-in-notification", false);

    FreeStyleBuild build = r.buildAndAssertSuccess(job);
    String log = r.getLog(build);

    // A built-in notification leaves the raw-template field unset, so logging that field printed
    // literally "null" for every build since the raw mode was added.
    assertFalse(log.contains("发送的消息详情，null"), () -> "still logging the wrong field:\n" + log);
    assertTrue(log.contains("ACTION_CARD"), () -> "message type missing from the log:\n" + log);
    assertTrue(
        log.contains("payload-marker built-in-notification"),
        () -> "logged text is not the expanded payload:\n" + log);
  }

  @Test
  void logsTheExpandedRawPayloadInsteadOfTheTemplate(JenkinsRule r) throws Exception {
    registerRobot(r);
    FreeStyleProject job = jobNotifying(r, "raw-notification", true);

    FreeStyleBuild build = r.buildAndAssertSuccess(job);
    String log = r.getLog(build);

    assertTrue(log.contains("MARKDOWN"), () -> "message type missing from the log:\n" + log);
    // The unexpanded template is already visible on the preceding "当前机器人信息" line, so only the
    // expanded form distinguishes the payload from the configuration.
    assertTrue(
        log.contains("payload-marker raw-notification"),
        () -> "logged text is not the expanded payload:\n" + log);
  }
}
