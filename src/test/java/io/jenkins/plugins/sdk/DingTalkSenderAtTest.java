package io.jenkins.plugins.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import io.jenkins.plugins.CapturingWebhook;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.MessageModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * Covers which mention tokens end up in the message body.
 *
 * <p>DingTalk only resolves a mention when the {@code at} object <em>and</em> a matching token in the
 * body are both present, so the body is what these tests assert on.
 */
class DingTalkSenderAtTest {

  private static final String MOBILE = "13800000000";
  private static final String AT_ALL = "@所有人";

  private static DingTalkSender senderFor(CapturingWebhook webhook) {
    return new DingTalkSender(
        new DingTalkRobotConfig("robot", "robot", webhook.url(), new ArrayList<>()), null);
  }

  private static Set<String> mobiles(String... values) {
    return new LinkedHashSet<>(List.of(values));
  }

  private static String textOf(JsonObject payload, String type) {
    return payload.getAsJsonObject(type).get("text").getAsString();
  }

  @Test
  void appendsTheAtAllTokenSoDingTalkResolvesIt() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendActionCard(MessageModel.builder()
          .type(MsgTypeEnum.ACTION_CARD)
          .atAll(true)
          .title("t")
          .text("# build ok")
          .build());

      assertTrue(textOf(webhook.onlyPayload(), "actionCard").contains(AT_ALL));
    }
  }

  /**
   * DingTalk stops resolving individual mobiles once everyone is mentioned, so a mobile appended
   * alongside {@code @所有人} would stay a bare phone number in the message body.
   */
  @Test
  void leavesMobilesOutWhenEveryoneIsMentioned() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendMarkdown(MessageModel.builder()
          .type(MsgTypeEnum.MARKDOWN)
          .atAll(true)
          .atMobiles(mobiles(MOBILE))
          .title("t")
          .text("# build ok")
          .build());

      String text = textOf(webhook.onlyPayload(), "markdown");
      assertTrue(text.contains(AT_ALL), () -> "@所有人 missing: " + text);
      assertFalse(text.contains(MOBILE), () -> "bare phone number left in the body: " + text);
    }
  }

  @Test
  void doesNotRepeatAnAtAllTokenTheTextAlreadyCarries() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendMarkdown(MessageModel.builder()
          .type(MsgTypeEnum.MARKDOWN)
          .atAll(true)
          .title("t")
          .text("heads up " + AT_ALL + " please review")
          .build());

      String text = textOf(webhook.onlyPayload(), "markdown");
      assertEquals(1, StringUtils.countMatches(text, AT_ALL), () -> "duplicated mention: " + text);
    }
  }

  @Test
  void doesNotRepeatAMobileTheTextAlreadyMentions() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendMarkdown(MessageModel.builder()
          .type(MsgTypeEnum.MARKDOWN)
          .atMobiles(mobiles(MOBILE))
          .title("t")
          .text("start @" + MOBILE + " end")
          .build());

      String text = textOf(webhook.onlyPayload(), "markdown");
      assertEquals(1, StringUtils.countMatches(text, "@" + MOBILE), () -> "duplicated: " + text);
    }
  }

  @Test
  void appendsMobilesTheTextDoesNotMention() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendMarkdown(MessageModel.builder()
          .type(MsgTypeEnum.MARKDOWN)
          .atMobiles(mobiles(MOBILE))
          .title("t")
          .text("nothing mentioned here")
          .build());

      assertTrue(textOf(webhook.onlyPayload(), "markdown").contains("@" + MOBILE));
    }
  }

  /**
   * DingTalk renders a text message's mention from the at object by itself, and appends its own copy
   * even when the content already carries the token — so anything added here shows up twice.
   */
  @Test
  void addsNoMentionToTextMessagesBecauseDingTalkRendersThem() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendText(MessageModel.builder()
          .type(MsgTypeEnum.TEXT)
          .atAll(true)
          .atMobiles(mobiles(MOBILE))
          .text("build finished")
          .build());

      JsonObject payload = webhook.onlyPayload();
      String content = payload.getAsJsonObject("text").get("content").getAsString();
      assertEquals("build finished", content, () -> "content was rewritten: " + content);
      // The at object still has to arrive — it is what DingTalk renders the mention from.
      assertTrue(payload.getAsJsonObject("at").get("isAtAll").getAsBoolean(), () -> "" + payload);
    }
  }

  @Test
  void addsNoMentionToLinkMessagesBecauseDingTalkIgnoresThem() throws IOException {
    try (CapturingWebhook webhook = new CapturingWebhook()) {
      senderFor(webhook).sendLink(MessageModel.builder()
          .type(MsgTypeEnum.LINK)
          .atAll(true)
          .atMobiles(mobiles(MOBILE))
          .title("t")
          .text("see the build")
          .messageUrl("https://example.com")
          .build());

      String body = webhook.bodies().get(0);
      assertFalse(body.contains(MOBILE), () -> "link carries a mention it cannot deliver: " + body);
      assertFalse(body.contains(AT_ALL), () -> "link carries a mention it cannot deliver: " + body);
    }
  }
}
