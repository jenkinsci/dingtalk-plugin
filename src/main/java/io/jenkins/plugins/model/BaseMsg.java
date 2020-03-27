package io.jenkins.plugins.model;

import com.dingtalk.api.request.OapiRobotSendRequest.At;
import java.util.ArrayList;
import java.util.Set;
import lombok.Data;

/**
 * @author liuwei
 * @date 2020/3/27 17:05
 * @desc 消息的通用信息
 */
@Data
public class BaseMsg {

  private String title;

  private String text;

  private Set<String> atMobiles;

  private Boolean isAtAll;

  public At getAt() {
    At at = new At();
    at.setAtMobiles(new ArrayList<>(atMobiles));
    return at;
  }
}
