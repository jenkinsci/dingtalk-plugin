package io.jenkins.plugins.tools;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.RobotConfigModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * 消息发送器
 *
 * @author liuwei
 * @date 2019/12/26 10:07
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
    RobotConfigModel robotConfigModel = RobotConfigModel.of(robot);
    this.title = "Jenkins 通知：" + robotConfigModel.getKeys();
    this.client = new DefaultDingTalkClient(robotConfigModel.getServer());
  }


  public String send(BuildJobModel buildJobModel) {

    List<Btns> btnsList = new ArrayList<>();
    String changeLog = buildJobModel.getChangeLog();
    String console = buildJobModel.getConsole();

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
    actionCard.setText(buildJobModel.getText());
    actionCard.setBtnOrientation("1");
    actionCard.setBtns(btnsList);

    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype(MSG_TYPE);
    request.setActionCard(actionCard);
    request.setAt(buildJobModel.getAt());

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
