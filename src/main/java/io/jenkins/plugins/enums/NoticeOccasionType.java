package io.jenkins.plugins.enums;

import lombok.Getter;

/**
 * @author liuwei
 * @date 2019/12/19 11:28
 * @desc
 */
@SuppressWarnings("all")
public enum NoticeOccasionType {
  START("在启动构建时通知"),

  CANCEL("构建中断时通知"),

  FAILURE("构建失败时通知"),

  UNSTABLE("构建不稳定时通知"),

  NOT_BUILT("在未构建时通知"),

  SUCCESS("构建成功时通知");

  @Getter
  private String desc;

  NoticeOccasionType(String desc) {
    this.desc = desc;
  }
}
