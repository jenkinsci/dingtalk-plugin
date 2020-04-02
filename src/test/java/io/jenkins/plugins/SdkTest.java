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
import org.apache.commons.codec.binary.Base64;

/**
 * @author liuwei
 * @date 2020/3/28 17:40
 */

public class SdkTest {

  public static void main(String[] args) {
//    testDingTalk();
//    AntdColor.GEEK_BLUE.show();
  }

  static void testDingTalk() {
    try {
      Long timestamp = System.currentTimeMillis();
      String secret = "SEC34778ce7a08f78577a2d11f8436d9ba67060e64acc8f0095afb61babaa31683b";

      String stringToSign = timestamp + "\n" + secret;
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
      byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
      String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
      DingTalkClient client = new DefaultDingTalkClient(
          "https://oapi.dingtalk.com/robot/send?access_token=d3e797903a606e7a19f510dc22863b87112e6783556bdbd264e9267d445b4e81&timestamp="
              + timestamp + "&sign=" + sign);
      OapiRobotSendRequest request = new OapiRobotSendRequest();
      request.setMsgtype("actionCard");
      OapiRobotSendRequest.Actioncard msg = new OapiRobotSendRequest.Actioncard();
      msg.setTitle("杭州天气");
      msg.setText("### 杭州天气" +
          "\n\n" +
          "9度，西北风1级，空气良89，相对温度73%" +
          "\n" +
          "---" +
          "\n" +
          "---" +
          "\n- " +
          Arrays.stream(BuildStatusEnum.values())
              .map(item -> Utils.dye(item.getLabel(), item.getColor()))
              .collect(
                  Collectors.joining("\n- ")
              ) +
          "\n" +
          "---" +
          "\n" +
          "<font color=#2f54eb>" +
          "@18516600940" +
          "</font>" +
          "\n" +
          "---" +
          "\n" +
          "10点20分发布 [天气](http://www.thinkpage.cn/)");
      At at = new At();
      at.setAtMobiles(Collections.singletonList("18516600940"));
      request.setAt(at);
      request.setActionCard(msg);
      OapiRobotSendResponse response = client.execute(request);
      System.out.println(response.getErrmsg());
    } catch (Exception e) {
      System.out.println(e);
    }

  }
}
