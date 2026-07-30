package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.FreeStyleProject;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Covers the job configuration form round-tripping the notifier list.
 *
 * <p>This view renders the per-item structure of lib/form/hetero-list itself, so the names and
 * classes the hetero-list script and the form submission rely on are this plugin's to keep right.
 * Saving the page unchanged has to give back what was there, and the header the list is drawn with
 * has to name the robot, since that is the only thing distinguishing one item from another.
 */
@WithJenkins
class DingTalkJobPropertyFormTest {

  private static FreeStyleProject jobWithTwoRobots(JenkinsRule r) throws Exception {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig("robot-a", "RobotA", "http://127.0.0.1:1/a", new ArrayList<>()));
    robots.add(new DingTalkRobotConfig("robot-b", "RobotB", "http://127.0.0.1:1/b", new ArrayList<>()));
    DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

    ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
    // The two differ in every field the form round-trips, so a value going missing cannot be
    // masked by the other notifier's copy of it.
    notifiers.add(notifier("robot-a", "RobotA", false, false, NoticeOccasionEnum.UNSTABLE));
    notifiers.add(notifier("robot-b", "RobotB", true, true, NoticeOccasionEnum.SUCCESS,
        NoticeOccasionEnum.FAILURE));

    FreeStyleProject job = r.createFreeStyleProject("round-trip");
    job.addProperty(new DingTalkJobProperty(notifiers));
    return job;
  }

  private static DingTalkNotifierConfig notifier(
      String id, String name, boolean raw, boolean disabled, NoticeOccasionEnum... occasions) {
    Set<String> names = new HashSet<>();
    for (NoticeOccasionEnum occasion : occasions) {
      // Not Set.of(): JEP-200 class filtering allows ImmutableCollections$List12/ListN but has
      // no Set or Map counterpart, so saving the job config would fail.
      names.add(occasion.name());
    }
    return new DingTalkNotifierConfig(
        raw, disabled, true, id, name, false, "13800138000",
        "content of " + name, "message of " + name, names);
  }

  private static DingTalkNotifierConfig find(List<DingTalkNotifierConfig> all, String name) {
    return all.stream()
        .filter(n -> name.equals(n.getRobotName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("no notifier for " + name));
  }

  private static JenkinsRule.WebClient client(JenkinsRule r) {
    JenkinsRule.WebClient wc = r.createWebClient();
    wc.getOptions().setThrowExceptionOnScriptError(false);
    wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
    return wc;
  }

  @Test
  void namesEachRobotInItsOwnChunkHeader(JenkinsRule r) throws Exception {
    HtmlPage page = client(r).getPage(jobWithTwoRobots(r), "configure");

    List<String> headers = new ArrayList<>();
    page.querySelectorAll("div.repeated-chunk[name=notifierConfigs] .repeated-chunk__header")
        .forEach(node -> headers.add(node.asNormalizedText().trim()));

    assertEquals(2, headers.size(), "one header per configured robot");
    assertTrue(headers.get(0).startsWith("RobotA"), "header names the robot, got: " + headers.get(0));
    assertTrue(headers.get(1).startsWith("RobotB"), "header names the robot, got: " + headers.get(1));

    assertEquals(
        2,
        page.querySelectorAll(
                "div.repeated-chunk[name=notifierConfigs] .jenkins-repeated-chunk__content")
            .size(),
        "each chunk wraps its body the way lib/form/hetero-list does");
  }

  @Test
  void savingTheFormUnchangedKeepsEveryNotifier(JenkinsRule r) throws Exception {
    FreeStyleProject job = jobWithTwoRobots(r);
    JenkinsRule.WebClient wc = client(r);
    HtmlForm form = wc.getPage(job, "configure").getFormByName("config");
    r.submit(form);

    DingTalkJobProperty saved = job.getProperty(DingTalkJobProperty.class);
    assertNotNull(saved, "the property survives a save");

    List<DingTalkNotifierConfig> notifiers = saved.getCheckedNotifierConfigs();
    assertEquals(2, notifiers.size(), "both robots are still selected after saving");

    for (DingTalkNotifierConfig notifier : notifiers) {
      String name = notifier.getRobotName();
      assertEquals("content of " + name, notifier.getContent(), name + " keeps its custom content");
      assertEquals("message of " + name, notifier.getMessage(), name + " keeps its custom message");
      assertEquals("13800138000", notifier.getAtMobile(), name + " keeps its mentions");
      assertTrue(notifier.isChecked(), name + " stays selected");
    }

    DingTalkNotifierConfig a = find(notifiers, "RobotA");
    DingTalkNotifierConfig b = find(notifiers, "RobotB");

    assertFalse(a.isRaw(), "RobotA keeps the built-in message");
    assertTrue(b.isRaw(), "RobotB keeps it disabled");
    assertFalse(a.isDisabled(), "RobotA stays enabled");
    assertTrue(b.isDisabled(), "RobotB stays disabled");

    // getNoticeOccasions() falls back to the global default, which is every occasion, so a set
    // that went missing would come back as all six rather than as null.
    assertEquals(
        new HashSet<>(List.of(NoticeOccasionEnum.UNSTABLE.name())),
        a.getNoticeOccasions(),
        "RobotA keeps the single occasion it was saved with");
    assertEquals(
        new HashSet<>(List.of(NoticeOccasionEnum.SUCCESS.name(), NoticeOccasionEnum.FAILURE.name())),
        b.getNoticeOccasions(),
        "RobotB keeps the two occasions it was saved with");
  }
}
