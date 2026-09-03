package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hudson.model.FreeStyleProject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.FakeChangeLogSCM;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/** Covers the build's latest commit reaching the card and the COMMIT_* variables. */
@WithJenkins
class DingTalkRunListenerChangeTest {

  private static final String ROBOT_ID = "robot-under-test";
  private static final String VARIABLES =
      "[${COMMIT_ID}][${COMMIT_TITLE}][${COMMIT_AUTHOR}]";

  private static FreeStyleProject job(
      JenkinsRule r,
      CapturingWebhook webhook,
      String name,
      FakeChangeLogSCM scm,
      String rawMessage,
      String... occasions)
      throws Exception {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig(ROBOT_ID, "robot", webhook.url(), new ArrayList<>()));
    DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

    ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
    notifiers.add(
        new DingTalkNotifierConfig(
            rawMessage != null,
            false,
            true,
            ROBOT_ID,
            "robot",
            false,
            null,
            null,
            rawMessage,
            new HashSet<>(List.of(occasions))));

    FreeStyleProject job = r.createFreeStyleProject(name);
    job.setScm(scm);
    job.addProperty(new DingTalkJobProperty(notifiers));
    return job;
  }

  private static FakeChangeLogSCM twoChanges() {
    FakeChangeLogSCM scm = new FakeChangeLogSCM();
    scm.addChange().withAuthor("alice").withMsg("first commit");
    scm.addChange().withAuthor("bob").withMsg("second commit");
    return scm;
  }

  private static String text(JsonObject payload, String type) {
    return payload.getAsJsonObject(type).get("text").getAsString();
  }

  @Test
  void cardNamesTheLatestChange(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(job(r, webhook, "with-changes", twoChanges(), null, "SUCCESS"));

      String text = text(webhook.onlyPayload(), "actionCard");
      assertTrue(text.endsWith("\n- 变更：second commit（bob）"), () -> text);
      assertFalse(text.contains("first commit"), () -> text);
    }
  }

  @Test
  void cardHasNoChangeLineWhenNothingChanged(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(
          job(r, webhook, "without-changes", new FakeChangeLogSCM(), null, "SUCCESS"));

      assertFalse(text(webhook.onlyPayload(), "actionCard").contains("变更"));
    }
  }

  @Test
  void startNoticeGoesOutBeforeTheChangelogExists(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(
          job(r, webhook, "start-and-success", twoChanges(), null, "START", "SUCCESS"));

      List<String> bodies = webhook.bodies();
      assertEquals(2, bodies.size(), bodies::toString);
      JsonObject start = JsonParser.parseString(bodies.get(0)).getAsJsonObject();
      JsonObject success = JsonParser.parseString(bodies.get(1)).getAsJsonObject();
      assertFalse(text(start, "actionCard").contains("变更"), () -> bodies.get(0));
      assertTrue(
          text(success, "actionCard").contains("- 变更：second commit（bob）"), () -> bodies.get(1));
    }
  }

  @Test
  void rawMessageExpandsTheChangeVariables(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(job(r, webhook, "raw-with-changes", twoChanges(), VARIABLES, "SUCCESS"));

      assertEquals("[][second commit][bob]", text(webhook.onlyPayload(), "markdown"));
    }
  }

  @Test
  void changeVariablesAreEmptyWithoutChanges(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      r.buildAndAssertSuccess(
          job(r, webhook, "raw-without-changes", new FakeChangeLogSCM(), VARIABLES, "SUCCESS"));

      assertEquals("[][][]", text(webhook.onlyPayload(), "markdown"));
    }
  }
}
