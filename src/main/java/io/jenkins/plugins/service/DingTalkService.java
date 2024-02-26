package io.jenkins.plugins.service;

import io.jenkins.plugins.DingTalkGlobalConfig;
import io.jenkins.plugins.DingTalkRobotConfig;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.sdk.DingTalkSender;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发送消息
 *
 * @author liuwei
 */
public class DingTalkService {

    private static volatile DingTalkService instance;
    private final Map<String, DingTalkSender> senders = new ConcurrentHashMap<>();

    public static DingTalkService getInstance() {
        if (instance == null) {
            synchronized (DingTalkService.class) {
                if (instance == null) {
                    instance = new DingTalkService();
                }
            }
        }
        return instance;
    }

    private DingTalkSender getSender(String robotId) {
        DingTalkSender sender = senders.get(robotId);
        if (sender == null) {
            synchronized (senders) {
                sender = senders.get(robotId);
                if (sender == null) {
                    DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
                    Proxy proxy = globalConfig.getProxy();
                    ArrayList<DingTalkRobotConfig> robotConfigs = globalConfig.getRobotConfigs();
                    Optional<DingTalkRobotConfig> robotConfigOptional = robotConfigs.stream()
                            .filter(item -> robotId.equals(item.getId()))
                            .findAny();
                    if (robotConfigOptional.isPresent()) {
                        DingTalkRobotConfig robotConfig = robotConfigOptional.get();
                        sender = new DingTalkSender(robotConfig, proxy);
                        senders.put(robotId, sender);
                    }
                }
            }
        }
        return sender;
    }

    public void resetSenders() {
        senders.clear();
    }

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
