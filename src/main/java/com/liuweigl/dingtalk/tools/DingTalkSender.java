package com.liuweigl.dingtalk.tools;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.liuweigl.dingtalk.DingTalkRobotConfig;
import com.liuweigl.dingtalk.model.BuildMessage;
import com.liuweigl.dingtalk.model.RobotConfigMeta;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * @author liuwei
 * @date 2019/12/26 10:07
 * @desc 消息发送器
 */
public class DingTalkSender {

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

    List<Btns> btnsList = new ArrayList<>();
    String changeLog = message.getChangeLog();
    String console = message.getConsole();

    if (!StringUtils.isEmpty(changeLog)) {
      Btns changeLogBtn = new Btns();
      changeLogBtn.setTitle("更改记录");
      changeLogBtn.setActionURL(changeLog);
      btnsList.add(changeLogBtn);
    }

    if (!StringUtils.isEmpty(console)) {
      Btns consoleBtn = new Btns();
      consoleBtn.setTitle("控制台");
      consoleBtn.setActionURL(console);
      btnsList.add(consoleBtn);
    }

    OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
    actionCard.setTitle(this.title);
    actionCard.setText(message.getText());
    actionCard.setBtnOrientation("1");
    actionCard.setBtns(btnsList);

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype(MSG_TYPE);
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
