package io.jenkins.plugins.model;

import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @author liuwei
 * @date 2020/3/27 17:13
 * @desc ActionCard 消息
 */
@Data
@Builder
public class ActionCardMsg extends BaseMsg {

  /**
   * 单个按钮的方案。(设置此项和singleURL后btns无效)
   */
  private String singleTitle;

  /**
   * 点击singleTitle按钮触发的URL
   */
  private String singleURL;

  /**
   * 按钮的信息：title-按钮方案，actionURL-点击按钮触发的URL
   */
  private List<Btns> btns;

  /**
   * 0-按钮竖直排列，1-按钮横向排列
   */
  private String btnOrientation;

  /**
   * 0-正常发消息者头像，1-隐藏发消息者头像
   */
  private String hideAvatar;

}
