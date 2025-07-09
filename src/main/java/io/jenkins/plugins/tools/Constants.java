package io.jenkins.plugins.tools;

/**
 * Constants class for DingTalk plugin configuration.
 * Contains message types, content types, and API endpoints used by the DingTalk integration.
 */
public class Constants {

    // Message Types
    public static final String MSG_TYPE_TEXT = "text";
    public static final String MSG_TYPE_LINK = "link";
    public static final String MSG_TYPE_MARKDOWN = "markdown";
    public static final String MSG_TYPE_ACTION_CARD = "actionCard";
    public static final String MSG_TYPE_FEED_CARD = "feedCard";

    // Content Types
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    // API Endpoints
    public static final String DINGTALK_WEBHOOK_URL_PREFIX = "https://oapi.dingtalk.com/robot/send?access_token=";
}
