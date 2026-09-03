package io.jenkins.plugins.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.google.gson.JsonObject;
import io.jenkins.plugins.CapturingWebhook;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.ButtonModel;
import io.jenkins.plugins.model.MessageModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Covers the form the button URLs of an actionCard are sent in. */
class DingTalkSenderButtonTest {

  private static final String URL = "https://ci.example.test/job/a b/1/console";
  private static final String WRAPPED =
      "dingtalk://dingtalkclient/action/open_platform_link"
          + "?pcLink=dingtalk%3A%2F%2Fdingtalkclient%2Fpage%2Flink%3Furl%3D"
          + "https%253A%252F%252Fci.example.test%252Fjob%252Fa%2520b%252F1%252Fconsole%26pc_slide%3Dfalse"
          + "&mobileLink=dingtalk%3A%2F%2Fdingtalkclient%2Faction%2Fjump%3Furl%3D"
          + "https%253A%252F%252Fci.example.test%252Fjob%252Fa%2520b%252F1%252Fconsole";

  private static DingTalkSender senderFor(CapturingWebhook webhook) {
    return new DingTalkSender(
        new DingTalkRobotConfig("robot", "robot", webhook.url(), new ArrayList<>()), null);
  }

  @Test
  void wrapsAnHttpUrlForBothClients() {
    assertEquals(WRAPPED, DingTalkSender.browserLink(URL));
  }

  @Test
  void leavesDingTalkLinksAndEmptyValuesAlone() {
    String own = "dingtalk://dingtalkclient/page/link?url=x&pc_slide=true";
    assertSame(own, DingTalkSender.browserLink(own));
    assertEquals("", DingTalkSender.browserLink(""));
    assertNull(DingTalkSender.browserLink(null));
  }

  @Test
  void buttonsGoOutWrapped() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      List<ButtonModel> btns = new ArrayList<>();
      btns.add(ButtonModel.of("console", URL));
      senderFor(webhook)
          .sendActionCard(
              MessageModel.builder().type(MsgTypeEnum.ACTION_CARD).title("t").text("b").btns(btns).build());

      JsonObject card = webhook.onlyPayload().getAsJsonObject("actionCard");
      assertEquals(
          WRAPPED, card.getAsJsonArray("btns").get(0).getAsJsonObject().get("actionURL").getAsString());
    }
  }

  @Test
  void theSingleButtonGoesOutWrapped() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook)
          .sendActionCard(
              MessageModel.builder()
                  .type(MsgTypeEnum.ACTION_CARD)
                  .title("t")
                  .text("b")
                  .singleTitle("open")
                  .singleUrl(URL)
                  .build());

      JsonObject card = webhook.onlyPayload().getAsJsonObject("actionCard");
      assertEquals(WRAPPED, card.get("singleURL").getAsString());
    }
  }
}
