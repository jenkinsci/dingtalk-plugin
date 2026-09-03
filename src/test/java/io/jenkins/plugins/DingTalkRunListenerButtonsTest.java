package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/** Covers the default buttons of the built-in card reaching DingTalk in the browser-opening form. */
@WithJenkins
class DingTalkRunListenerButtonsTest {

  private static final String ROBOT_ID = "robot-under-test";

  /** The target behind {@code open_platform_link → pcLink → page/link → url}. */
  private static String pcTarget(String actionUrl) {
    String pcLink = query(URI.create(actionUrl), "pcLink");
    return query(URI.create(pcLink), "url");
  }

  private static String query(URI uri, String name) {
    for (String pair : uri.getRawQuery().split("&")) {
      if (pair.startsWith(name + "=")) {
        return URLDecoder.decode(pair.substring(name.length() + 1), StandardCharsets.UTF_8);
      }
    }
    throw new AssertionError(name + " missing from " + uri);
  }

  @Test
  void defaultButtonsOpenTheBuildPagesInTheBrowser(JenkinsRule r) throws Exception {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
      robots.add(new DingTalkRobotConfig(ROBOT_ID, "robot", webhook.url(), new ArrayList<>()));
      DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);
      ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
      notifiers.add(
          new DingTalkNotifierConfig(
              false, false, true, ROBOT_ID, "robot", false, null, null, null,
              new HashSet<>(List.of(NoticeOccasionEnum.SUCCESS.name()))));
      FreeStyleProject job = r.createFreeStyleProject("buttons");
      job.addProperty(new DingTalkJobProperty(notifiers));

      FreeStyleBuild build = r.buildAndAssertSuccess(job);

      JsonArray btns = webhook.onlyPayload().getAsJsonObject("actionCard").getAsJsonArray("btns");
      assertEquals(2, btns.size(), btns::toString);
      for (int i = 0; i < 2; i++) {
        String actionUrl = btns.get(i).getAsJsonObject().get("actionURL").getAsString();
        assertTrue(
            actionUrl.startsWith("dingtalk://dingtalkclient/action/open_platform_link?"), actionUrl);
        assertTrue(pcTarget(actionUrl).startsWith(build.getAbsoluteUrl()), actionUrl);
      }
      assertTrue(pcTarget(btns.get(0).getAsJsonObject().get("actionURL").getAsString()).endsWith("/changes"));
      assertTrue(pcTarget(btns.get(1).getAsJsonObject().get("actionURL").getAsString()).endsWith("/console"));
    }
  }
}
