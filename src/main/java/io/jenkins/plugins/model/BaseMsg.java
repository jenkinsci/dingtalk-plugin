package io.jenkins.plugins.model;

import com.dingtalk.api.request.OapiRobotSendRequest.At;
import java.util.ArrayList;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author liuwei
 * @date 2020/3/27 17:05
 * @desc 消息的通用信息
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseMsg {

  protected  String type;

  private String title;

  private String text;

  private Set<String> atMobiles;

  private Boolean isAtAll;

  public At getAt() {
    At at = new At();
    at.setIsAtAll(String.valueOf(isAtAll));
    at.setAtMobiles(new ArrayList<>(atMobiles));
    return at;
  }
}
