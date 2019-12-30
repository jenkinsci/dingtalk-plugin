package com.liuweigl.dingtalk.service.impl;

import com.liuweigl.dingtalk.DingTalkGlobalConfig;
import com.liuweigl.dingtalk.DingTalkRobotConfig;
import com.liuweigl.dingtalk.model.BuildMessage;
import com.liuweigl.dingtalk.service.DingTalkService;
import com.liuweigl.dingtalk.tools.DingTalkSender;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author liuwei
 * @date 2019/12/26 10:45
 * @desc service
 */
public class DingTalkServiceImpl implements DingTalkService {

  private Map<String, DingTalkSender> senders = new ConcurrentHashMap<>();


  @Override
  public String send(String robotId, BuildMessage message) {
    DingTalkSender sender = senders.get(robotId);
    if (sender == null) {
      DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
      CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs = globalConfig.getRobotConfigs();
      Optional<DingTalkRobotConfig> robotConfigOptional = robotConfigs.stream()
          .filter(item -> robotId.equals(item.getId())).findAny();
      if (robotConfigOptional.isPresent()) {
        DingTalkRobotConfig robotConfig = robotConfigOptional.get();
        sender = new DingTalkSender(robotConfig);
        senders.put(robotId, sender);
      }
    }
    if (sender != null) {
      return sender.send(message);
    }
    return null;
  }
}
