package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import lombok.ToString;

/**
 * 安全策略
 *
 * @author liuwei
 */
@ToString
public enum SecurityPolicyEnum {

  /** 关键字 */
  KEY,

  /** 加签 */
  SECRET;

  public String getDesc() {
    return switch (this) {
      case KEY -> Messages.SecurityPolicyType_key();
      case SECRET -> Messages.SecurityPolicyType_secret();
    };
  }
}
