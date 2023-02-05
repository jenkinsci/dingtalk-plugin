package io.jenkins.plugins.sdk;

import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.model.RobotConfigModel;
import io.jenkins.plugins.sdk.DingTalkRobotRequest.ActionCard;
import io.jenkins.plugins.sdk.DingTalkRobotRequest.At;
import io.jenkins.plugins.sdk.DingTalkRobotRequest.Link;
import io.jenkins.plugins.sdk.DingTalkRobotRequest.Markdown;
import io.jenkins.plugins.sdk.DingTalkRobotRequest.Text;
import io.jenkins.plugins.tools.AntdColor;
import io.jenkins.plugins.tools.Utils;
import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * 消息发送器
 *
 * @author liuwei
 */
@Slf4j
public class DingTalkSender {

  private final RobotConfigModel robotConfigModel;

  private final Proxy proxy;

  public DingTalkSender(DingTalkRobotConfig robotConfig, Proxy proxy) {
    this.robotConfigModel = RobotConfigModel.of(robotConfig);
    this.proxy = proxy;
  }

  /**
   * 发送 text 类型的消息
   *
   * @param msg 消息
   * @return 异常信息
   */
  public String sendText(MessageModel msg) {
    At at = msg.getAt();
    Text text = new Text();
    text.setAt(at);
    text.setContent(addKeyWord(addAtInfo(msg.getText(), at, false)));

    return call(text);
  }

  /**
   * 发送 link 类型的消息
   *
   * @param msg 消息
   * @return 异常信息
   */
  public String sendLink(MessageModel msg) {
    At at = msg.getAt();
    Link link = new Link();
    link.setAt(at);
    link.setTitle(addKeyWord(msg.getTitle()));
    link.setText(addAtInfo(msg.getText(), at, false));
    link.setMessageUrl(msg.getMessageUrl());
    link.setPicUrl(msg.getPicUrl());

    return call(link);
  }

  public String sendMarkdown(MessageModel msg) {
    At at = msg.getAt();
    Markdown markdown = new Markdown();
    markdown.setAt(at);
    markdown.setTitle(addKeyWord(msg.getTitle()));
    markdown.setText(addAtInfo(msg.getText(), at, true));

    return call(markdown);
  }

  public String sendActionCard(MessageModel msg) {
    At at = msg.getAt();
    ActionCard actioncard = new ActionCard();
    actioncard.setAt(at);
    actioncard.setTitle(addKeyWord(msg.getTitle()));
    actioncard.setText(addAtInfo(msg.getText(), at, true));
    String singleTitle = msg.getSingleTitle();
    if (StringUtils.isEmpty(singleTitle)) {
      actioncard.setBtns(msg.getRobotBtns());
    } else {
      actioncard.setSingleTitle(singleTitle);
      actioncard.setSingleURL(msg.getSingleUrl());
    }
    actioncard.setBtnOrientation(msg.getBtnOrientation());
    actioncard.setHideAvatar(msg.getHideAvatar());

    return call(actioncard);
  }

  /**
   * 统一处理请求
   *
   * @param request 请求
   * @return 异常信息
   */
  private String call(DingTalkRobotRequest request) {
    try {
      String server = robotConfigModel.getServer();
      Map<String, Object> content = request.getParams();

      HttpResponse response =
          HttpRequest.builder()
              .server(server)
              .data(content)
              .method(Constants.METHOD_POST)
              .proxy(proxy)
              .build()
              .request();
      String body = response.getBody();
      DingTalkRobotResponse data = Utils.fromJson(body, DingTalkRobotResponse.class);
      if (data.getErrcode() != 0) {
        log.error("钉钉消息发送失败：{}", body);
        return body;
      }
    } catch (IOException e) {
      log.error("钉钉消息发送失败", e);
      return ExceptionUtils.getStackTrace(e);
    }
    return null;
  }

  /**
   * 添加关键字
   *
   * @param str 原始内容
   * @return 带关键字的信息
   */
  private String addKeyWord(String str) {
    String keys = robotConfigModel.getKeys();
    if (StringUtils.isEmpty(keys)) {
      return str;
    }
    return str + " " + keys;
  }

  /**
   * 添加 at 信息
   *
   * @param content 原始内容
   * @param at at 配置
   * @param markdown 是否是 markdown 格式的内容
   * @return 包含 at 信息的内容
   */
  private String addAtInfo(String content, At at, boolean markdown) {
    List<String> atMobiles = at.getAtMobiles();
    if (atMobiles == null || atMobiles.isEmpty()) {
      return content;
    }
    String atContent = "@" + StringUtils.join(atMobiles, " @");
    if (markdown) {
      return content + "\n\n" + Utils.dye(atContent, AntdColor.BLUE.toString()) + "\n";
    }
    return content + atContent;
  }
}
