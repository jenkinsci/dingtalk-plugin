package io.jenkins.plugins.service;

import io.jenkins.plugins.model.MessageModel;

/**
 * 发送消息
 *
 * @author liuwei
 */
public interface DingTalkService {

  String send(String robot, MessageModel msg);
}
