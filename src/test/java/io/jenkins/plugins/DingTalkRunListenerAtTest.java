package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import hudson.model.FreeStyleProject;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/** Covers the mention settings reaching the payload for both notification modes. */
@WithJenkins
class DingTalkRunListenerAtTest {

  private static final String ROBOT_ID = "robot-under-test";
  private static final String MOBILE = "13800000000";

  private static FreeStyleProject jobNotifying(
      JenkinsRule r, CapturingWebhook webhook, String name, boolean raw) throws Exception {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig(ROBOT_ID, "robot", webhook.url(), new ArrayList<>()));
    DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

    DingTalkNotifierConfig notifier = new DingTalkNotifierConfig(
        raw,
        false,
        true,
        ROBOT_ID,
        "robot",
        false,
        MOBILE,
        raw ? null : "built-in body",
        raw ? "custom body" : null,
        // Not Set.of(): JEP-200 class filtering allows ImmutableCollections$List12/ListN but has
        // no Set or Map counterpart, so saving the job config would fail.
        new HashSet<>(List.of(NoticeOccasionEnum.SUCCESS.name())));
    ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
    notifiers.add(notifier);

    FreeStyleProject job = r.createFreeStyleProject(name);
    job.addProperty(new DingTalkJobProperty(notifiers));
    return job;
  }

  /**
   * A mention needs both halves to arrive: the mobile in the {@code at} object decides who gets
   * notified, the token in the body is what DingTalk resolves into a name.
   */
  private static void assertMentionsConfiguredPeople(JsonObject payload, String type) {
    JsonObject at = payload.getAsJsonObject("at");
    assertEquals(
        MOBILE,
        at.getAsJsonArray("atMobiles").get(0).getAsString(),
        () -> "atMobiles not sent: " + payload);

    String text = payload.getAsJsonObject(type).get("text").getAsString();
    assertTrue(text.contains("@" + MOBILE), () -> "mobile missing from the body: " + text);
  }

  /**
   * The raw branch used to build its MessageModel without atAll/atMobiles, so "disable built-in
   * message" silently dropped every mention.
   */
  @Test
  void rawNotificationCarriesTheConfiguredMentions(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(jobNotifying(r, webhook, "raw-notification", true));

      assertMentionsConfiguredPeople(webhook.onlyPayload(), "markdown");
    }
  }

  @Test
  void builtInNotificationCarriesTheConfiguredMentions(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(jobNotifying(r, webhook, "built-in-notification", false));

      assertMentionsConfiguredPeople(webhook.onlyPayload(), "actionCard");
    }
  }
}
