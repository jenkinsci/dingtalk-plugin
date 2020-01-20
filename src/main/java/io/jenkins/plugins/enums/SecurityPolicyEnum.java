package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import lombok.Getter;
import lombok.ToString;

/**
 * 安全策略
 *
 * @author liuwei
 * @date 2020/1/19 12:02
 */

@ToString
public enum SecurityPolicyEnum {
  /**
   * ip 地址/段
   */
  IP(Messages.SecurityPolicyType_ip()),

  /**
   * 关键字
   */
  KEY(Messages.SecurityPolicyType_key()),

  /**
   * 加密
   */
  SECRET(Messages.SecurityPolicyType_secret());

  @Getter
  private String desc;

  SecurityPolicyEnum(String desc) {
    this.desc = desc;
  }
}
