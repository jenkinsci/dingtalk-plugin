package com.ztbsuper.dingding;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import hudson.EnvVars;
import hudson.ProxyConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String apiUrl = "https://oapi.dingtalk.com/robot/send";

    private String api;

    private String accessToken;

    private String secret;

    private JSONObject msgType;

    private EnvVars env;

    public DingdingServiceImpl(String jenkinsURL, String token, boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort, TaskListener listener, AbstractBuild build, String secret, JSONObject msgType,
        Map vars) {
        this.jenkinsURL = jenkinsURL;
        this.accessToken = token;
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort =  onAbort;
        this.listener = listener;
        this.build = build;
        this.secret = secret;
        long timestamp = System.currentTimeMillis();
        String sign = getSign(timestamp, secret);
        this.api = apiUrl + "?sign=" + sign + "&timestamp=" + timestamp;
        this.msgType = msgType;
        this.env = new EnvVars(vars);
    }

    @Override
    public void start() {
        String pic = "http://icon-park.com/imagefiles/loading7_gray.gif";
        String title = String.format("%s%s开始构建", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]开始构建", build.getProject().getDisplayName(), build.getDisplayName());

        String link = getBuildUrl();
        if (onStart) {
            logger.info("send link msg from " + listener.toString());
            if("markdown".equals(msgType.getString("value"))) {
                sendMarkdownMessage(link,title);
            } else {
                sendLinkMessage(link, content, title, pic);
            }
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
        String title = String.format("%s%s构建成功", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]构建成功, summary:%s, duration:%s", build.getProject().getDisplayName(), build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onSuccess) {
            logger.info("send link msg from " + listener.toString());
            if("markdown".equals(msgType.getString("value"))) {
                sendMarkdownMessage(link,title);
            } else {
                sendLinkMessage(link, content, title, pic);
            }
        }
    }

    @Override
    public void failed() {
        String pic = "http://www.iconsdb.com/icons/preview/soylent-red/x-mark-3-xxl.png";
        String title = String.format("%s%s构建失败", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]构建失败, summary:%s, duration:%s", build.getProject().getDisplayName(), build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onFailed) {
            logger.info("send link msg from " + listener.toString());
            if("markdown".equals(msgType.getString("value"))) {
                sendMarkdownMessage(link,title);
            } else {
                sendLinkMessage(link, content, title, pic);
            }
        }
    }

    @Override
    public void abort() {
        String pic = "http://www.iconsdb.com/icons/preview/soylent-red/x-mark-3-xxl.png";
        String title = String.format("%s%s构建中断", build.getProject().getDisplayName(), build.getDisplayName());
        String content = String.format("项目[%s%s]构建中断, summary:%s, duration:%s", build.getProject().getDisplayName(), build.getDisplayName(), build.getBuildStatusSummary().message, build.getDurationString());

        String link = getBuildUrl();
        logger.info(link);
        if (onAbort) {
            logger.info("send link msg from " + listener.toString());
            if("markdown".equals(msgType.getString("value"))) {
                sendMarkdownMessage(link,title);
            } else {
                sendLinkMessage(link, content, title, pic);
            }
        }
    }

    private void sendTextMessage(String msg) {

    }

    private void sendMarkdownMessage(String link,String title) {
        DingTalkClient client = new DefaultDingTalkClient(api);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText("# " + title + "\n" +
            "----\n" +
            env.expand(msgType.getString("markdownText")) + "\n" +
            "----\n" +
            "[" + title + "](" + link +")\n");
        request.setMarkdown(markdown);

        try {
            OapiRobotSendResponse response = client.execute(request,accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLinkMessage(String link, String msg, String title, String pic) {
        DingTalkClient client = new DefaultDingTalkClient(api);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        request.setMsgtype("link");
        OapiRobotSendRequest.Link requestLink = new OapiRobotSendRequest.Link();
        requestLink.setMessageUrl(link);
        requestLink.setPicUrl(pic);
        requestLink.setTitle(title);
        requestLink.setText(msg);
        request.setLink(requestLink);

        try {
            OapiRobotSendResponse response = client.execute(request,accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String getSign(Long timestamp,String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String encode = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            return encode;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
