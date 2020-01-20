package io.jenkins.plugins.service;

import io.jenkins.plugins.model.BuildJobModel;

/**
 * @author liuwei
 * @date 2019/12/23 14:47
 * @desc 发送消息
 */
public interface DingTalkService {

  String send(String robot, BuildJobModel buildJobModel);
}
