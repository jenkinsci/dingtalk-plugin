package io.jenkins.plugins.tools;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiRobotSendRequest.Actioncard;
import com.dingtalk.api.request.OapiRobotSendRequest.At;
import com.dingtalk.api.request.OapiRobotSendRequest.Link;
import com.dingtalk.api.request.OapiRobotSendRequest.Markdown;
import com.dingtalk.api.request.OapiRobotSendRequest.Text;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.model.RobotConfigModel;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * 消息发送器
 *
 * @author liuwei
 * @date 2019/12/26 10:07
 */
public class DingTalkSender {

  private RobotConfigModel robotConfigModel;

  public DingTalkSender(DingTalkRobotConfig robot) {
    this.robotConfigModel = RobotConfigModel.of(robot);
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
    text.setContent(
        addKeyWord(
            addAtInfo(
                msg.getText(),
                at,
                false
            )
        )
    );

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("text");
    request.setAt(at);
    request.setText(text);
    return call(request);
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
    link.setTitle(
        addKeyWord(
            msg.getTitle()
        )
    );
    link.setText(
        addAtInfo(
            msg.getText(),
            at,
            false
        )
    );
    link.setMessageUrl(
        msg.getMessageUrl()
    );
    link.setPicUrl(
        msg.getPicUrl()
    );

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("link");
    request.setLink(link);
    request.setAt(at);
    return call(request);
  }

  public String sendMarkdown(MessageModel msg) {
    At at = msg.getAt();
    Markdown markdown = new Markdown();
    markdown.setTitle(
        addKeyWord(
            msg.getTitle()
        )
    );
    markdown.setText(
        addAtInfo(
            msg.getText(),
            at,
            true
        )
    );

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("markdown");
    request.setAt(at);
    request.setMarkdown(markdown);
    return call(request);
  }

  public String sendActionCard(MessageModel msg) {
    At at = msg.getAt();
    Actioncard actioncard = new Actioncard();
    actioncard.setTitle(
        addKeyWord(
            msg.getTitle()
        )
    );
    actioncard.setText(
        addAtInfo(
            msg.getText(),
            at,
            true
        )
    );
    String singleTitle = msg.getSingleTitle();
    if (StringUtils.isEmpty(singleTitle)) {
      actioncard.setBtns(msg.getRobotBtns());
    } else {
      actioncard.setSingleTitle(singleTitle);
      actioncard.setSingleURL(
          msg.getSingleUrl()
      );
    }
    actioncard.setBtnOrientation(
        msg.getBtnOrientation()
    );
    actioncard.setHideAvatar(
        msg.getHideAvatar()
    );

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("actionCard");
    request.setAt(at);
    request.setActionCard(actioncard);
    return call(request);
  }


  /**
   * 统一处理请求
   *
   * @param request 请求
   * @return 异常信息
   */
  private String call(OapiRobotSendRequest request) {
    try {
      OapiRobotSendResponse response = new DefaultDingTalkClient(
          robotConfigModel.getServer()
      )
          .execute(request);
      if (!response.isSuccess()) {
        return response.getErrmsg();
      }
    } catch (ApiException e) {
      return e.getMessage();
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
   * @param content  原始内容
   * @param at       at 配置
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
