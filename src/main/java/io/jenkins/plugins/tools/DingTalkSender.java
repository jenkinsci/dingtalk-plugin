package io.jenkins.plugins.tools;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiRobotSendRequest.At;
import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.ActionCardMsg;
import io.jenkins.plugins.model.RobotConfigModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * @author liuwei
 * @date 2019/12/26 10:07
 * @desc 消息发送器
 */
public class DingTalkSender {

  /**
   * 在标题里包含关键字验证
   */
  private String title;

  private TaobaoClient client;


  public DingTalkSender(DingTalkRobotConfig robot) {
    RobotConfigModel robotConfigModel = RobotConfigModel.of(robot);
    this.title = "Jenkins 通知：" + robotConfigModel.getKeys();
    this.client = new DefaultDingTalkClient(robotConfigModel.getServer());
  }

  /**
   * 卡片类型消息
   *
   * @param message 消息
   * @return 异常消息
   */
  public String send(ActionCardMsg message) {


//    if (!StringUtils.isEmpty(changeLog)) {
//      Btns changeLogBtn = new Btns();
//      changeLogBtn.setTitle("更改记录");
//      changeLogBtn.setActionURL(changeLog);
//      btnList.add(changeLogBtn);
//    }
//
//    if (!StringUtils.isEmpty(console)) {
//      Btns consoleBtn = new Btns();
//      consoleBtn.setTitle("控制台");
//      consoleBtn.setActionURL(console);
//      btnList.add(consoleBtn);
//    }

    OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
    actionCard.setTitle(title);
    actionCard.setText(text);
    actionCard.setBtnOrientation("1");
    actionCard.setBtns(btnList);

    At at = new At();
    at.setAtMobiles(new ArrayList<>(mobiles));

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype(MSG_TYPE);
    request.setActionCard(actionCard);
    request.setAt(at);

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

  public String send(TextMsg)

}
