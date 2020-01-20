package io.jenkins.plugins.service.impl;

import io.jenkins.plugins.DingTalkGlobalConfig;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.service.DingTalkService;
import io.jenkins.plugins.tools.DingTalkSender;
import java.util.Map;
import java.util.Optional;
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
  public String send(String robotId, BuildJobModel buildJobModel) {
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
      return sender.send(buildJobModel);
    }
    return null;
  }
}
