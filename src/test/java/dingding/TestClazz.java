package dingding;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

/**
 * @Author: margo
 * @Date: 2019/11/6 17:55
 * @Description:
 */
public class TestClazz {

    String secret = "";
    String access_token = "";

    @Test
    public void test1()
        throws ApiException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        long timestamp = System.currentTimeMillis();
        String sign = getSign(timestamp,secret);
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign=" + sign + "&timestamp=" + timestamp);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

//        request.setMsgtype("text");
//        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
//        text.setContent("测试文本消息");
//        request.setText(text);
//        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//        at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
//        request.setAt(at);

//        request.setMsgtype("link");
//        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
//        link.setMessageUrl("https://www.dingtalk.com/");
//        link.setPicUrl("");
//        link.setTitle("时代的火车向前开");
//        link.setText("这个即将发布的新版本，创始人xx称它为“红树林”。\n" +
//                    "而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是“红树林");
//        request.setLink(link);

        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("杭州天气");
        markdown.setText("#### 杭州天气 @156xxxx8827\n" +
                    "> 9度，西北风1级，空气良89，相对温度73%\n\n" +
                    "> ![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png)\n"  +
                    "> ###### 10点20分发布 [天气](http://www.thinkpage.cn/) \n");
        request.setMarkdown(markdown);

        OapiRobotSendResponse response = client.execute(request,access_token);
    }

    @Test
    public void test2()
        throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        long timestamp = System.currentTimeMillis();
        String sign = getSign(timestamp,secret);
        System.out.println("sign = " + sign + ", timestamp = " + timestamp);
    }

    public String getSign(Long timestamp,String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException  {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String encode = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        return encode;
    }

}
