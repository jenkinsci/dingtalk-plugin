package com.liuweigl.dingtalk.service;

import com.liuweigl.dingtalk.model.BuildMessage;

/**
 * @author liuwei
 * @date 2019/12/23 14:47
 * @desc 发送消息
 */
public interface DingTalkService {

  String send(String robot, BuildMessage message);
}
