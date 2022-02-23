package io.jenkins.plugins.service.impl;

import io.jenkins.plugins.DingTalkGlobalConfig;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.DingTalkSender;
import io.jenkins.plugins.service.DingTalkService;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * service
 *
 * @author liuwei
 */
public class DingTalkServiceImpl implements DingTalkService {

  private Map<String, DingTalkSender> senders = new ConcurrentHashMap<>();

  private DingTalkSender getSender(String robotId) {
    DingTalkSender sender = senders.get(robotId);
    if (sender == null) {
      DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
      Proxy proxy = globalConfig.getProxy();
      ArrayList<DingTalkRobotConfig> robotConfigs = globalConfig.getRobotConfigs();
      Optional<DingTalkRobotConfig> robotConfigOptional =
          robotConfigs.stream().filter(item -> robotId.equals(item.getId())).findAny();
      if (robotConfigOptional.isPresent()) {
        DingTalkRobotConfig robotConfig = robotConfigOptional.get();
        sender = new DingTalkSender(robotConfig, proxy);
        senders.put(robotId, sender);
      }
    }
    return sender;
  }

  @Override
  public String send(String robotId, MessageModel msg) {
    MsgTypeEnum type = msg.getType();
    DingTalkSender sender = getSender(robotId);
    if (sender == null) {
      return String.format("ID 为 %s 的机器人不存在。", robotId);
    }
    if (type == null) {
      return "消息类型【type】不能为空";
    }
    switch (type) {
      case TEXT:
        return sender.sendText(msg);
      case LINK:
        return sender.sendLink(msg);
      case MARKDOWN:
        return sender.sendMarkdown(msg);
      case ACTION_CARD:
        return sender.sendActionCard(msg);
      default:
        return String.format("错误的消息类型：%s", type.name());
    }
  }
}
