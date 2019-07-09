package com.ztbsuper.dingding;

import com.alibaba.fastjson.JSONObject;
import hudson.EnvVars;
import hudson.ProxyConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Marvin on 16/10/8.
 */
public class DingdingServiceImpl implements DingdingService {

    private Logger logger = LoggerFactory.getLogger(DingdingService.class);

    private String jenkinsURL;

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private boolean onAbort;

    private TaskListener listener;

    private AbstractBuild build;

    private static final String apiUrl = "https://oapi.dingtalk.com/robot/send?access_token=";

    private String tokens;

    private String displayName;

    private String envStr;

    public DingdingServiceImpl(String jenkinsURL, String tokens, String displayName, String environment,
        boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort,
        TaskListener listener, AbstractBuild build) {

        String customizedDisplayName = getExpandedValue(displayName, listener, build);
        String envName = getExpandedValue(environment, listener, build);

        this.jenkinsURL = jenkinsURL;
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort =  onAbort;
        this.listener = listener;
        this.build = build;
        this.tokens = tokens;
        this.displayName = StringUtils.isNotEmpty(customizedDisplayName) ? customizedDisplayName : build.getProject().getDisplayName();
        this.envStr = StringUtils.isNotEmpty(envName) ? '[' + envName + ']' : "";
    }

    @Override
    public void start() {
        String pic = "http://icon-park.com/imagefiles/loading7_gray.gif";
        String title = String.format("%s%s开始构建%s", displayName, build.getDisplayName(), envStr);
        String content = String.format("项目[%s%s]开始构建", displayName, build.getDisplayName());

        String link = getBuildUrl();
        if (onStart) {
            logger.info("send link msg from " + listener.toString());
            sendLinkMessage(link, content, title, pic);
        }

    }

    private String getBuildUrl() {
        if (jenkinsURL.endsWith("/")) {
            return jenkinsURL + build.getUrl();
        } else {
            return jenkinsURL + "/" + build.getUrl();
        }
    }

    @Override
    public void success() {
        String pic = "http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/sign-check-icon.png";
        String title = String.format("%s%s构建成功%s", displayName, build.getDisplayName(), envStr);
        String content = String.format("项目[%s%s]构建成功, summary:%s, duration:%s", displayName, build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onSuccess) {
            logger.info("send link msg from " + listener.toString());
            sendLinkMessage(link, content, title, pic);
        }
    }

    @Override
    public void failed() {
        String pic = "http://www.iconsdb.com/icons/preview/soylent-red/x-mark-3-xxl.png";
        String title = String.format("%s%s构建失败%s", displayName, build.getDisplayName(), envStr);
        String content = String.format("项目[%s%s]构建失败, summary:%s, duration:%s", displayName, build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onFailed) {
            logger.info("send link msg from " + listener.toString());
            sendLinkMessage(link, content, title, pic);
        }
    }

    @Override
    public void abort() {
        String pic = "http://www.iconsdb.com/icons/preview/soylent-red/x-mark-3-xxl.png";
        String title = String.format("%s%s构建中断%s", displayName, build.getDisplayName(), envStr);
        String content = String.format("项目[%s%s]构建中断, summary:%s, duration:%s", displayName, build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onAbort) {
            logger.info("send link msg from " + listener.toString());
            sendLinkMessage(link, content, title, pic);
        }
    }

    /**
     * 获取参数值
     *
     * @param paramName 参数名，可能是变量
     * @param listener 任务监听器
     * @param build 构建对象
     * @return 参数值
     */
    private String getExpandedValue(String paramName, TaskListener listener, AbstractBuild build) {
        EnvVars envVars = null;
        try {
            envVars = build.getEnvironment(listener);
        } catch (IOException | InterruptedException e) {
            logger.error("getEnvironment", e);
        }
        return envVars != null ? envVars.expand(paramName) : paramName;
    }

    private void sendTextMessage(String msg) {

    }

    private void sendLinkMessage(String link, String msg, String title, String pic) {
        String[] tokenArray = tokens.split(",", -1);
        for (String token : tokenArray) {
            doSendLinkMessage(apiUrl + token, link, msg, title, pic);
        }
    }

    private void doSendLinkMessage(String uri, String link, String msg, String title, String pic) {
        HttpClient client = getHttpClient();
        PostMethod post = new PostMethod(uri);

        JSONObject body = new JSONObject();
        body.put("msgtype", "link");

        JSONObject linkObject = new JSONObject();
        linkObject.put("text", msg);
        linkObject.put("title", title);
        linkObject.put("picUrl", pic);
        linkObject.put("messageUrl", link);

        body.put("link", linkObject);
        try {
            post.setRequestEntity(new StringRequestEntity(body.toJSONString(), "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("build request error", e);
        }
        try {
            client.executeMethod(post);
            logger.info(post.getResponseBodyAsString());
        } catch (IOException e) {
            logger.error("send msg error", e);
        }
        post.releaseConnection();
    }


    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null && jenkins.proxy != null) {
            ProxyConfiguration proxy = jenkins.proxy;
            if (proxy != null && client.getHostConfiguration() != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    client.getState().setProxyCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return client;
    }
}
