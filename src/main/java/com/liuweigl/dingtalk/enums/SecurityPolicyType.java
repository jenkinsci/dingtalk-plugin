package com.liuweigl.dingtalk.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
@SuppressWarnings("all")
public enum SecurityPolicyType {

  KEY("关键字"),

  SECRET("加密"),

  IP("IP 地址/段");

  @Getter
  private String desc;

  SecurityPolicyType(String desc) {
    this.desc = desc;
  }
}
