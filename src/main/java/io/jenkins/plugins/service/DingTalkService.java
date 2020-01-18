package io.jenkins.plugins.service;

import io.jenkins.plugins.DingTalkNotifier;
import io.jenkins.plugins.model.BuildMessage;
import java.util.Collection;

/**
 * @author liuwei
 * @date 2019/12/23 14:47
 * @desc 发送消息
 */
public interface DingTalkService {

  Collection<String> send(DingTalkNotifier notifier, BuildMessage message);
}
