package io.jenkins.plugins.tools;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.BuildMessage;
import io.jenkins.plugins.model.RobotConfigMeta;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;

/**
 * @author liuwei
 * @date 2019/12/26 10:07
 * @desc 消息发送器
 */
public class DingTalkSender {

  /**
   * 卡片按钮标题
   */
  public static final String SINGLE_TITLE = "查看";

  /**
   * 消息类型
   */
  public static final String MSG_TYPE = "actionCard";

  /**
   * 在标题里包含关键字验证
   */
  private String title;

  private TaobaoClient client;


  public DingTalkSender(DingTalkRobotConfig robot) {
    RobotConfigMeta meta = RobotConfigMeta.of(robot);
    this.title = "Jenkins 通知：" + meta.getKeys();
    this.client = new DefaultDingTalkClient(meta.getServer());
  }


  public String send(BuildMessage message) {
    OapiRobotSendRequest request = new OapiRobotSendRequest();
    OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
    request.setMsgtype(MSG_TYPE);
    actionCard.setTitle(this.title);
    actionCard.setText(message.getText());
    actionCard.setSingleURL(message.getDetailUrl());
    actionCard.setSingleTitle(SINGLE_TITLE);
    request.setActionCard(actionCard);
    request.setAt(message.getAt());
    try {
      OapiRobotSendResponse response = this.client.execute(request);
      if (!response.isSuccess()) {
        return response.getErrmsg();
      }
    } catch (ApiException e) {
      return e.getMessage();
    }
    return null;
  }
}
