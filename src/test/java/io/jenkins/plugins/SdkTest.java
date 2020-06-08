package io.jenkins.plugins;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiRobotSendRequest.At;
import com.dingtalk.api.response.OapiRobotSendResponse;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.tools.Utils;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

/**
 * @author liuwei
 * @date 2020/3/28 17:40
 */

public class SdkTest {



  public static void main(String ...args) {
//    try {
//      Long timestamp = System.currentTimeMillis();
//      String secret = "SEC34778ce7a08f78577a2d11f8436d9ba67060e64acc8f0095afb61babaa31683b";
//
//      String stringToSign = timestamp + "\n" + secret;
//      Mac mac = Mac.getInstance("HmacSHA256");
//      mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
//      byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
//      String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
//      DingTalkClient client = new DefaultDingTalkClient(
//          "https://oapi.dingtalk.com/robot/send?access_token=af0f18ea706e6205c59ecc32011f3a2b5a87da8f565abf3e9033043d496438f9");
//      OapiRobotSendRequest request = new OapiRobotSendRequest();
//      request.setMsgtype("actionCard");
//      OapiRobotSendRequest.Actioncard msg = new OapiRobotSendRequest.Actioncard();
//      msg.setTitle("jenkins");
//      msg.setText("![](https://jenkins.eastcoal.club/static/celesea_weapp_preview.png)");
//      request.setActionCard(msg);
//      OapiRobotSendResponse response = client.execute(request);
//      System.out.println(response.getErrmsg());
//    } catch (Exception e) {
//      System.out.println(e);
//    }

    System.out.println("测试构建消息1\\n- 昂哈哈昂昂昂昂昂\\n- 嘟嘟嘟叔叔嘟嘟双杜甫\\n".replaceAll("\\\\n","\n"));
  }
}
