package io.jenkins.plugins.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author liuwei
 */
@Data
@NoArgsConstructor
public abstract class DingTalkRobotRequest {

  /** 被@人的手机号 */
  private At at;

  /**
   * 消息类型
   *
   * @return type
   */
  public abstract String getMsgType();

  public Map<String, Object> getParams() {
    String msgType = this.getMsgType();
    Map<String, Object> params = new HashMap<>(16);
    params.put("msgtype", msgType);
    params.put(msgType, this);
    params.put("at", this.at);
    return params;
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
   * text类型
   *
   * @author top auto create
   * @since 1.0, null
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @NoArgsConstructor
  public static class Text extends DingTalkRobotRequest {
    /** text类型 */
    private String content;

    @Override
    public String getMsgType() {
      return Constants.MSG_TYPE_TEXT;
    }
  }

  /**
   * 消息类型，此时固定为:link
   *
   * @author top auto create
   * @since 1.0, null
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @NoArgsConstructor
  public static class Link extends DingTalkRobotRequest {
    /** 点击消息跳转的URL */
    private String messageUrl;
    /** 图片URL */
    private String picUrl;
    /** 消息内容。如果太长只会部分展示 */
    private String text;
    /** 消息标题 */
    private String title;

    @Override
    public String getMsgType() {
      return Constants.MSG_TYPE_LINK;
    }
  }

  /**
   * 此消息类型为固定markdown
   *
   * @author top auto create
   * @since 1.0, null
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @NoArgsConstructor
  public static class Markdown extends DingTalkRobotRequest {
    /** markdown格式的消息 */
    private String text;
    /** 首屏会话透出的展示内容 */
    private String title;

    @Override
    public String getMsgType() {
      return Constants.MSG_TYPE_MARKDOWN;
    }
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
  @EqualsAndHashCode(callSuper = true)
  @Data
  @NoArgsConstructor
  public static class ActionCard extends DingTalkRobotRequest {
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

    @Override
    public String getMsgType() {
      return Constants.MSG_TYPE_ACTION_CARD;
    }
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
  @EqualsAndHashCode(callSuper = true)
  @Data
  @NoArgsConstructor
  public static class FeedCard extends DingTalkRobotRequest {
    /** links */
    private List<Links> links;

    @Override
    public String getMsgType() {
      return Constants.MSG_TYPE_FEED_CARD;
    }
  }
}
