package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hudson.model.FreeStyleProject;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.MockBuilder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/** Covers which notice a build that recovers from a failure produces for each notifier. */
@WithJenkins
class DingTalkRunListenerFixedTest {

  private static DingTalkNotifierConfig notifier(String robotId, NoticeOccasionEnum... occasions) {
    HashSet<String> names = new HashSet<>();
    for (NoticeOccasionEnum occasion : occasions) {
      names.add(occasion.name());
    }
    return new DingTalkNotifierConfig(
        false, false, true, robotId, robotId, false, null, null, null, names);
  }

  private static String statusIn(CapturingWebhook webhook) {
    List<String> bodies = webhook.bodies();
    assertEquals(1, bodies.size(), bodies::toString);
    JsonObject card = JsonParser.parseString(bodies.get(0)).getAsJsonObject().getAsJsonObject("actionCard");
    return card.get("title").getAsString();
  }

  @Test
  void aRecoveredBuildIsFixedOnlyForNotifiersThatAskedForIt(JenkinsRule r) throws Exception {
    try (CapturingWebhook both = new CapturingWebhook();
        CapturingWebhook successOnly = new CapturingWebhook();
        CapturingWebhook fixedOnly = new CapturingWebhook()) {
      ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
      robots.add(new DingTalkRobotConfig("both", "both", both.url(), new ArrayList<>()));
      robots.add(new DingTalkRobotConfig("success-only", "success-only", successOnly.url(), new ArrayList<>()));
      robots.add(new DingTalkRobotConfig("fixed-only", "fixed-only", fixedOnly.url(), new ArrayList<>()));
      DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

      ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
      notifiers.add(notifier("both", NoticeOccasionEnum.SUCCESS, NoticeOccasionEnum.FIXED));
      notifiers.add(notifier("success-only", NoticeOccasionEnum.SUCCESS));
      notifiers.add(notifier("fixed-only", NoticeOccasionEnum.FIXED));
      FreeStyleProject job = r.createFreeStyleProject("recovering");
      job.addProperty(new DingTalkJobProperty(notifiers));

      job.getBuildersList().add(new FailureBuilder());
      r.buildAndAssertStatus(hudson.model.Result.FAILURE, job);
      assertTrue(both.bodies().isEmpty() && successOnly.bodies().isEmpty() && fixedOnly.bodies().isEmpty());

      job.getBuildersList().clear();
      r.buildAndAssertSuccess(job);

      String fixed = BuildStatusEnum.FIXED.getLabel();
      String success = BuildStatusEnum.SUCCESS.getLabel();
      assertTrue(statusIn(both).endsWith(" " + fixed), () -> statusIn(both));
      assertTrue(statusIn(successOnly).endsWith(" " + success), () -> statusIn(successOnly));
      assertTrue(statusIn(fixedOnly).endsWith(" " + fixed), () -> statusIn(fixedOnly));
    }
  }

  @Test
  void anAbortedBuildBetweenTheFailureAndTheRecoveryStillCountsAsFixed(JenkinsRule r) throws Exception {
    try (CapturingWebhook fixedOnly = new CapturingWebhook()) {
      ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
      robots.add(new DingTalkRobotConfig("fixed-only", "fixed-only", fixedOnly.url(), new ArrayList<>()));
      DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);
      ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
      notifiers.add(notifier("fixed-only", NoticeOccasionEnum.FIXED));
      FreeStyleProject job = r.createFreeStyleProject("recovering-after-an-abort");
      job.addProperty(new DingTalkJobProperty(notifiers));

      job.getBuildersList().add(new FailureBuilder());
      r.buildAndAssertStatus(hudson.model.Result.FAILURE, job);
      job.getBuildersList().clear();
      job.getBuildersList().add(new MockBuilder(hudson.model.Result.ABORTED));
      r.buildAndAssertStatus(hudson.model.Result.ABORTED, job);
      job.getBuildersList().clear();
      r.buildAndAssertSuccess(job);

      assertTrue(
          statusIn(fixedOnly).endsWith(" " + BuildStatusEnum.FIXED.getLabel()), () -> statusIn(fixedOnly));
    }
  }

  @Test
  void aSuccessAfterASuccessIsNotFixed(JenkinsRule r) throws Exception {
    try (CapturingWebhook fixedOnly = new CapturingWebhook()) {
      ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
      robots.add(new DingTalkRobotConfig("fixed-only", "fixed-only", fixedOnly.url(), new ArrayList<>()));
      DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);
      ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
      notifiers.add(notifier("fixed-only", NoticeOccasionEnum.FIXED));
      FreeStyleProject job = r.createFreeStyleProject("steady");
      job.addProperty(new DingTalkJobProperty(notifiers));

      r.buildAndAssertSuccess(job);
      r.buildAndAssertSuccess(job);

      assertTrue(fixedOnly.bodies().isEmpty(), () -> fixedOnly.bodies().toString());
    }
  }
}
