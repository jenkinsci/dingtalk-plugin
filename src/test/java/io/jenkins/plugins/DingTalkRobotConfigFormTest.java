package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import net.sf.json.JSONArray;
import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.WebResponseData;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.htmlunit.util.NameValuePair;
import org.htmlunit.util.WebConnectionWrapper;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.jvnet.localizer.Localizable;

/**
 * Covers the robot form's security policies end to end: what {@code robotConfigValidator.js} sends
 * to the test button's {@code doTest} for the policies on the form, saved or just added through the
 * list's menu, what the button shows for the reply, and that saving the form keeps the policies.
 */
@WithJenkins
class DingTalkRobotConfigFormTest {

  private static final String WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=token";
  private static final String SCRIPT = "robotConfigValidator.js";

  /** Errors raised by the plugin's own script; core scripts are noisy under HtmlUnit and stay muted. */
  private final List<String> scriptErrors = new ArrayList<>();

  private static JSONArray expected() {
    return JSONArray.fromObject(
        "[{\"$class\":\"" + KeySecurityPolicyConfig.class.getName() + "\",\"value\":\"build\"},"
            + "{\"$class\":\"" + SecretSecurityPolicyConfig.class.getName() + "\",\"value\":\"SECxxxx\"}]");
  }

  private HtmlElement robotForm(JenkinsRule r, ArrayList<DingTalkSecurityPolicyConfig> security)
      throws Exception {
    ArrayList<DingTalkRobotConfig> robots = new ArrayList<>();
    robots.add(new DingTalkRobotConfig("robot-a", "RobotA", WEBHOOK, security));
    DingTalkGlobalConfig.getInstance().setRobotConfigs(robots);

    JenkinsRule.WebClient wc = r.createWebClient();
    wc.getOptions().setThrowExceptionOnScriptError(false);
    wc.setJavaScriptErrorListener(
        new SilentJavaScriptErrorListener() {
          @Override
          public void scriptException(HtmlPage page, ScriptException e) {
            if (e.getMessage().contains(SCRIPT)) {
              scriptErrors.add(e.getMessage());
            }
          }
        });
    HtmlPage page = wc.goTo("dingtalk");
    return page.querySelector(".robot-config-container");
  }

  /**
   * Clicks the test button, answering doTest locally with the given reply (a null reply fails the
   * request) so nothing is sent to DingTalk. Returns the security policies the script sent.
   */
  private String press(HtmlElement robot, int status, String reply) throws Exception {
    HtmlPage page = (HtmlPage) robot.getPage();
    WebClient wc = page.getWebClient();
    AtomicReference<String> sent = new AtomicReference<>();
    String doTest = "/descriptorByName/" + DingTalkRobotConfig.class.getName() + "/test";
    // The wrapper installs itself on the client.
    new WebConnectionWrapper(wc) {
      @Override
      public WebResponse getResponse(WebRequest request) throws IOException {
        if (!request.getUrl().getPath().endsWith(doTest)) {
          return super.getResponse(request);
        }
        for (NameValuePair parameter : request.getParameters()) {
          if ("securityPolicyConfigs".equals(parameter.getName())) {
            sent.set(parameter.getValue());
          }
        }
        if (reply == null) {
          throw new IOException("connection refused");
        }
        return new WebResponse(
            new WebResponseData(reply.getBytes(StandardCharsets.UTF_8), status, "OK", new ArrayList<>()),
            request,
            0);
      }
    };

    HtmlElement button = robot.querySelector(".robot-config-validate-btn");
    button.click();
    wc.waitForBackgroundJavaScript(10_000);
    assertEquals(List.of(), scriptErrors, "the plugin script ran without errors");
    assertNotNull(sent.get(), "the button called doTest");
    return sent.get();
  }

  /** Clicks the test button with doTest replying OK; returns the security policies the script sent. */
  private JSONArray clickTest(HtmlElement robot) throws Exception {
    String sent = press(robot, 200, "ok");
    HtmlElement message = robot.querySelector(".robot-config-validate-msg");
    assertEquals("ok", message.asNormalizedText(), "the reply reached the page");
    assertTrue(message.getAttribute("class").contains("jenkins-alert-success"));
    return JSONArray.fromObject(sent);
  }

  private static HtmlElement menuItem(HtmlPage page, String text) {
    for (DomNode item : page.querySelectorAll(".jenkins-dropdown__item")) {
      if (text.equals(item.asNormalizedText().trim())) {
        return (HtmlElement) item;
      }
    }
    throw new AssertionError("no menu entry " + text);
  }

  @Test
  void testButtonSendsTheSavedPolicies(JenkinsRule r) throws Exception {
    ArrayList<DingTalkSecurityPolicyConfig> security = new ArrayList<>();
    security.add(new KeySecurityPolicyConfig("build"));
    security.add(new SecretSecurityPolicyConfig("SECxxxx"));

    assertEquals(expected(), clickTest(robotForm(r, security)));
  }

  @Test
  void testButtonSendsPoliciesAddedButNotSavedYet(JenkinsRule r) throws Exception {
    HtmlElement robot = robotForm(r, new ArrayList<>());
    HtmlPage page = (HtmlPage) robot.getPage();
    WebClient wc = page.getWebClient();

    // The menu names each policy type in the page's locale, which is en-US under HtmlUnit.
    List<Localizable> names =
        List.of(Messages._SecurityPolicyType_key(), Messages._SecurityPolicyType_secret());
    for (Localizable name : names) {
      HtmlElement add = robot.querySelector("button.hetero-list-add");
      add.click();
      wc.waitForBackgroundJavaScript(10_000);
      menuItem(page, name.toString(Locale.ENGLISH)).click();
      wc.waitForBackgroundJavaScript(10_000);
    }

    for (DomNode chunk : robot.querySelectorAll(".repeated-chunk[name=securityPolicyConfigs]")) {
      String descriptor = chunk.getAttributes().getNamedItem("descriptorid").getNodeValue();
      HtmlInput value = chunk.querySelector("input[name='_.value']");
      value.setValue(KeySecurityPolicyConfig.class.getName().equals(descriptor) ? "build" : "SECxxxx");
    }

    assertEquals(expected(), clickTest(robot));
  }

  @Test
  void testButtonShowsAFailedRequest(JenkinsRule r) throws Exception {
    HtmlElement robot = robotForm(r, new ArrayList<>());

    press(robot, 500, "<html>stack trace</html>");

    HtmlElement message = robot.querySelector(".robot-config-validate-msg");
    assertEquals("Error: HTTP 500", message.asNormalizedText());
    assertTrue(message.getAttribute("class").contains("jenkins-alert-danger"));
  }

  @Test
  void testButtonShowsALostConnection(JenkinsRule r) throws Exception {
    HtmlElement robot = robotForm(r, new ArrayList<>());

    press(robot, 0, null);

    HtmlElement message = robot.querySelector(".robot-config-validate-msg");
    assertTrue(message.asNormalizedText().startsWith("Error: "), message.asNormalizedText());
    assertTrue(message.getAttribute("class").contains("jenkins-alert-danger"));
  }

  /**
   * Entries the way 2.8.0 saved them: untyped, one per policy type, the unused one blank. The blank
   * one is dropped and the other comes back typed.
   */
  @Test
  void savingLegacyPoliciesUnchangedKeepsTheSecretOnly(JenkinsRule r) throws Exception {
    ArrayList<DingTalkSecurityPolicyConfig> security = new ArrayList<>();
    security.add(new DingTalkSecurityPolicyConfig("KEY", "", "关键字"));
    security.add(new DingTalkSecurityPolicyConfig("SECRET", "SECxxxx", "加签"));
    HtmlElement robot = robotForm(r, security);
    HtmlPage page = (HtmlPage) robot.getPage();

    r.submit(page.getFormByName("dingTalkManagementLinkForm"));

    List<DingTalkRobotConfig> saved = DingTalkGlobalConfig.getInstance().getRobotConfigs();
    assertEquals(1, saved.size());
    List<DingTalkSecurityPolicyConfig> policies = saved.get(0).getSecurityPolicyConfigs();
    assertEquals(1, policies.size());
    assertTrue(policies.get(0) instanceof SecretSecurityPolicyConfig);
    assertEquals("SECRET", policies.get(0).getType());
    assertEquals("SECxxxx", policies.get(0).getValue());

    String xml =
        Files.readString(
            new File(r.jenkins.getRootDir(), DingTalkGlobalConfig.class.getName() + ".xml").toPath());
    assertTrue(xml.contains("<" + SecretSecurityPolicyConfig.class.getName() + ">"), xml);
    assertTrue(xml.contains("<type>SECRET</type>"), xml);
  }
}
