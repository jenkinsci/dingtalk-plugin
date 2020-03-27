package io.jenkins.plugins.service;

import io.jenkins.plugins.model.BaseMsg;

/**
 * 发送消息
 *
 * @author liuwei
 * @date 2019/12/23 14:47
 */
public interface DingTalkService {

  String send(String robot, BaseMsg msg);
}
