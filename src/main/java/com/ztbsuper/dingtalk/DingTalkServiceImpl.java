package com.ztbsuper.dingtalk;

import com.ztbsuper.dingding.DingdingService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import ren.wizard.dingtalkclient.DingTalkClient;
import ren.wizard.dingtalkclient.message.DingMessage;
import ren.wizard.dingtalkclient.message.LinkMessage;

public class DingTalkServiceImpl implements DingdingService {

    private Logger logger = LoggerFactory.getLogger(DingTalkServiceImpl.class);

    private String accessToken;
    private String notifyPeople;
    private String message;
    private String imageUrl;
    private String jenkinsUrl;

    private TaskListener listener;
    private AbstractBuild build;

    public DingTalkServiceImpl(String accessToken, String notifyPeople, String message, String imageUrl, String jenkinsUrl, TaskListener listener, AbstractBuild build) {
        this.accessToken = accessToken;
        this.notifyPeople = notifyPeople;
        this.message = message;
        this.imageUrl = imageUrl;
        this.jenkinsUrl = jenkinsUrl;
        this.listener = listener;
        this.build = build;
    }

    private void send(DingMessage message) {
        DingTalkClient dingTalkClient = DingTalkClient.getInstance();
        try {
            dingTalkClient.sendMessage(accessToken, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void success() {
        EnvVars envVars = null;
        try {
            envVars = build.getEnvironment(listener);
            String buildInfo = build.getFullDisplayName();
            if (!StringUtils.isBlank(message)) {
                send(LinkMessage.builder()
                        .title(buildInfo)
                        .picUrl(envVars.expand(imageUrl))
                        .text(envVars.expand(message))
                        .messageUrl(envVars.expand(jenkinsUrl))
                        .build());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("消息发送失败", e);
        }
    }

    @Override
    public void failed() {

    }

    @Override
    public void abort() {

    }
}
