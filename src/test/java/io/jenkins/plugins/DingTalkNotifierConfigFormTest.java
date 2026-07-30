package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.FreeStyleProject;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Covers which message textarea the job configuration form offers.
 *
 * <p>A notifier saved with the built-in message disabled sends `自定义消息` and nothing else, so that
 * is the block the form has to show; offering the other one sends edits to a field nothing reads.
 * Two robots are configured throughout because the blocks used to be looked up document-wide,
 * which only misbehaves once the markup is on the page more than once.
 *
 * <p>Both cases start from robots that are already part of the job. A notifier added through the
 * "add robot" menu takes the same path — the hetero-list script applies behaviours to the subtree it
 * inserts — but that menu is a dropdown these tests cannot open, so that half is covered by hand.
 */
@WithJenkins
class DingTalkNotifierConfigFormTest {

  private static final String BUILT_IN = "robot-built-in";
  private static final String RAW = "robot-raw";

  private static FreeStyleProject jobWithBothModes(JenkinsRule r) throws Exception {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig(BUILT_IN, "BuiltIn", "http://127.0.0.1:1/a", new ArrayList<>()));
    robots.add(new DingTalkRobotConfig(RAW, "Raw", "http://127.0.0.1:1/b", new ArrayList<>()));
    DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

    ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
    notifiers.add(notifier(BUILT_IN, "BuiltIn", false));
    notifiers.add(notifier(RAW, "Raw", true));

    FreeStyleProject job = r.createFreeStyleProject("form-under-test");
    job.addProperty(new DingTalkJobProperty(notifiers));
    return job;
  }

  private static DingTalkNotifierConfig notifier(String id, String name, boolean raw) {
    return new DingTalkNotifierConfig(
        raw,
        false,
        true,
        id,
        name,
        false,
        "",
        "content of " + name,
        "message of " + name,
        // Not Set.of(): JEP-200 class filtering allows ImmutableCollections$List12/ListN but has
        // no Set or Map counterpart, so saving the job config would fail.
        new HashSet<>(List.of(NoticeOccasionEnum.SUCCESS.name())));
  }

  /** The notifier a block belongs to is identified by the content its textarea was saved with. */
  private static DomElement blockHolding(HtmlPage page, String text) {
    for (DomNode node : page.querySelectorAll(".dt-raw-content-builtin, .dt-raw-content-custom")) {
      DomElement block = (DomElement) node;
      if (block.asXml().contains(text)) {
        return block;
      }
    }
    throw new AssertionError("no notifier block holds " + text);
  }

  private static boolean hidden(DomElement block) {
    return block.getAttribute("style").replace(" ", "").contains("display:none");
  }

  private static HtmlPage configurePage(JenkinsRule r, FreeStyleProject job) throws Exception {
    JenkinsRule.WebClient wc = r.createWebClient();
    wc.getOptions().setJavaScriptEnabled(true);
    wc.getOptions().setThrowExceptionOnScriptError(false);
    wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
    HtmlPage page = wc.getPage(job, "configure");
    wc.waitForBackgroundJavaScript(2000);
    return page;
  }

  @Test
  void offersTheBlockEachSavedNotifierActuallySends(JenkinsRule r) throws Exception {
    HtmlPage page = configurePage(r, jobWithBothModes(r));

    assertEquals(2, page.querySelectorAll(".dt-notifier-config-raw").size(), "both robots render");

    assertFalse(
        hidden(blockHolding(page, "content of BuiltIn")),
        "the notifier keeping the built-in message must offer 自定义内容");
    assertTrue(
        hidden(blockHolding(page, "message of BuiltIn")),
        "the notifier keeping the built-in message must not offer 自定义消息");

    assertFalse(
        hidden(blockHolding(page, "message of Raw")),
        "a notifier saved with the built-in message disabled must offer 自定义消息, since that is "
            + "the only field it sends");
    assertTrue(
        hidden(blockHolding(page, "content of Raw")),
        "a notifier saved with the built-in message disabled must not offer 自定义内容");
  }

  @Test
  void togglingOneNotifierLeavesTheOtherAlone(JenkinsRule r) throws Exception {
    // Both saved keeping the built-in message, then the *second* one is ticked. The direction
    // matters: unticking moves a notifier towards the markup's own default, so looking the block
    // up document-wide happens to land on the same result and proves nothing. Ticking the second
    // notifier is the transition where addressing the wrong notifier becomes visible.
    HtmlPage page = configurePage(r, jobWithNeitherRaw(r));

    List<DomNode> boxes = page.querySelectorAll(".dt-notifier-config-raw");
    assertEquals(2, boxes.size(), "both robots render");
    HtmlCheckBoxInput second = (HtmlCheckBoxInput) boxes.get(1);
    assertFalse(second.isChecked(), "the second notifier starts out keeping the built-in message");

    second.setChecked(true);

    assertTrue(
        hidden(blockHolding(page, "content of Second")),
        "ticking the second notifier must hide its own 自定义内容");
    assertFalse(
        hidden(blockHolding(page, "message of Second")),
        "ticking the second notifier must reveal its own 自定义消息");

    assertFalse(
        hidden(blockHolding(page, "content of First")),
        "the first notifier must be left alone");
    assertTrue(
        hidden(blockHolding(page, "message of First")),
        "the first notifier must be left alone");
  }

  private static FreeStyleProject jobWithNeitherRaw(JenkinsRule r) throws Exception {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig("robot-first", "First", "http://127.0.0.1:1/a", new ArrayList<>()));
    robots.add(new DingTalkRobotConfig("robot-second", "Second", "http://127.0.0.1:1/b", new ArrayList<>()));
    DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

    ArrayList<DingTalkNotifierConfig> notifiers = new ArrayList<>();
    notifiers.add(notifier("robot-first", "First", false));
    notifiers.add(notifier("robot-second", "Second", false));

    FreeStyleProject job = r.createFreeStyleProject("toggle-under-test");
    job.addProperty(new DingTalkJobProperty(notifiers));
    return job;
  }
}
