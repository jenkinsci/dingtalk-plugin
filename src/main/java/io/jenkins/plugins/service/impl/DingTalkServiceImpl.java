package io.jenkins.plugins.service.impl;

import io.jenkins.plugins.DingTalkGlobalConfig;
import io.jenkins.plugins.DingTalkNotifier;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.model.BuildMessage;
import io.jenkins.plugins.service.DingTalkService;
import io.jenkins.plugins.tools.DingTalkSender;
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
  public Collection<String> send(DingTalkNotifier notifier, BuildMessage message) {
    List<String> result = new ArrayList<>();
    Set<String> robots = notifier.getRobots();
    if (robots == null || robots.isEmpty()) {
      return result;
    }
    robots.forEach(id -> {
      DingTalkSender sender = senders.get(id);
      if (sender == null) {
        DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
        CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs = globalConfig.getRobotConfigs();
        Optional<DingTalkRobotConfig> robotConfigOptional = robotConfigs.stream()
            .filter(item -> id.equals(item.getId())).findAny();
        if (robotConfigOptional.isPresent()) {
          DingTalkRobotConfig robotConfig = robotConfigOptional.get();
          sender = new DingTalkSender(robotConfig);
          senders.put(id, sender);
        }
      }
      if (sender != null) {
        String msg = sender.send(message);
        if (msg != null) {
          result.add(msg);
        }
      }
    });
    return result;
  }
}
