package io.jenkins.plugins.sdk;

import io.jenkins.plugins.tools.Utils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuwei
 * @date 2020-12-09 20:06:47
 */
@Data
@NoArgsConstructor
public class DingTalkRobotRequest implements Serializable {

  /** 此消息类型为固定actionCard */
  private String actionCard;

  /** 被@人的手机号 */
  private String at;

  /** 此消息类型为固定feedCard */
  private String feedCard;

  /** 消息类型，此时固定为:link */
  private String link;

  /** 此消息类型为固定markdown */
  private String markdown;

  /** 消息类型 */
  private String msgtype;

  /** text类型 */
  private String text;

  public void setAt(At at) {
    this.at = Utils.toJson(at);
  }

  public void setActionCard(Actioncard actionCard) {
    this.msgtype = Constants.MSG_TYPE_ACTION_CARD;
    this.actionCard = Utils.toJson(actionCard);
  }

  public void setFeedCard(FeedCard feedCard) {
    this.msgtype = Constants.MSG_TYPE_FEED_CARD;
    this.feedCard = Utils.toJson(feedCard);
  }

  public void setLink(Link link) {
    this.msgtype = Constants.MSG_TYPE_LINK;
    this.link = Utils.toJson(link);
  }

  public void setMarkdown(Markdown markdown) {
    this.msgtype = Constants.MSG_TYPE_MARKDOWN;
    this.markdown = Utils.toJson(markdown);
  }

  public void setText(Text text) {
    this.msgtype = Constants.MSG_TYPE_TEXT;
    this.text = Utils.toJson(text);
  }

  public Map<String, String> getTextParams() {
    Map<String, String> txtParams = new HashMap<>(16);
    txtParams.put("actionCard", this.actionCard);
    txtParams.put("at", this.at);
    txtParams.put("feedCard", this.feedCard);
    txtParams.put("link", this.link);
    txtParams.put("markdown", this.markdown);
    txtParams.put("msgtype", this.msgtype);
    txtParams.put("text", this.text);
    return txtParams;
  }

  /**
   * text类型
   *
   * @author top auto create
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class Text {
    /** text类型 */
    private String content;
  }

  /**
   * 被@人的手机号
   *
   * @author top auto create
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class At {
    /** 被 @ 人的手机号 */
    private List<String> atMobiles;
    /** 是否 @ 所有人 */
    private Boolean isAtAll;
  }

  /**
   * 消息类型，此时固定为:link
   *
   * @author top auto create
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class Link {
    /** 点击消息跳转的URL */
    private String messageUrl;
    /** 图片URL */
    private String picUrl;
    /** 消息内容。如果太长只会部分展示 */
    private String text;
    /** 消息标题 */
    private String title;
  }

  /**
   * 此消息类型为固定markdown
   *
   * @author top auto create
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class Markdown {
    /** markdown格式的消息 */
    private String text;
    /** 首屏会话透出的展示内容 */
    private String title;
  }

  /**
   * 按钮的信息
   *
   * @author top auto create
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class Btns {
    /** 按钮方案， */
    private String actionURL;
    /** 点击按钮触发的URL此消息类型为固定feedCard */
    private String title;
  }

  /**
   * 此消息类型为固定 actionCard
   *
   * @author top auto create
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class Actioncard {
    /** 0-按钮竖直排列，1-按钮横向排列 */
    private String btnOrientation;
    /** 按钮的信息 */
    private List<Btns> btns;
    /** 0-正常发消息者头像,1-隐藏发消息者头像 */
    private String hideAvatar;
    /** 单个按钮的方案。(设置此项和singleURL后btns无效。) */
    private String singleTitle;
    /** 点击singleTitle按钮触发的URL */
    private String singleURL;
    /** markdown格式的消息 */
    private String text;
    /** 首屏会话透出的展示内容 */
    private String title;
  }

  /**
   * links
   *
   * @since 1.0, null
   */
  @Data
  @NoArgsConstructor
  public static class Links {
    /** 点击单条信息到跳转链接 */
    private String messageURL;
    /** 单条信息文本 */
    private String picURL;
    /** 单条信息后面图片的URL */
    private String title;
  }

  /**
   * 此消息类型为固定feedCard
   *
   * @since 1.0
   */
  @Data
  @NoArgsConstructor
  public static class FeedCard {
    /** links */
    private List<Links> links;

    public List<Links> getLinks() {
      return this.links;
    }

    public void setLinks(List<Links> links) {
      this.links = links;
    }
  }
}
