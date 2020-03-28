import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * @author liuwei
 * @date 2020/3/28 17:40
 */
public class SdkTest {

  public static void main(String[] args)
      throws ApiException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    Long timestamp = System.currentTimeMillis();
    String secret = "this is secret";

    String stringToSign = timestamp + "\n" + secret;
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
    byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
    String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    DingTalkClient client = new DefaultDingTalkClient(
        "https://oapi.dingtalk.com/robot/send?access_token=d3e797903a606e7a19f510dc22863b87112e6783556bdbd264e9267d445b4e81&timestamp="
            + timestamp + "&sign=" + sign);
    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("text");
    OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
    text.setContent("测试文本消息");
    request.setText(text);
    OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
    at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
    request.setAt(at);

    request.setMsgtype("link");
    OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
    link.setMessageUrl("https://www.dingtalk.com/");
    link.setPicUrl("");
    link.setTitle("时代的火车向前开");
    link.setText("这个即将发布的新版本，创始人xx称它为“红树林”。\n" +
        "而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是“红树林");
    request.setLink(link);

    OapiRobotSendResponse response = client.execute(request);
    System.out.println(response.getErrmsg());
  }
}
