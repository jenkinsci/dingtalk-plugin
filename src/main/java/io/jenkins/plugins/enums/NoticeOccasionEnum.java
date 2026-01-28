package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;

/**
 * 通知时机
 *
 * @author liuwei
 */
public enum NoticeOccasionEnum {
  /**
   * 在启动构建时通知
   */
  START,

  /**
   * 构建中断时通知
   */
  ABORTED,

  /**
   * 构建失败时通知
   */
  FAILURE,

  /**
   * 构建成功时通知
   */
  SUCCESS,

  /**
   * 构建不稳定时通知
   */
  UNSTABLE,

  /**
   * 在未构建时通知
   */
  NOT_BUILT;

  public String getDesc() {
    return switch (this) {
      case START -> Messages.NoticeOccasion_start();
      case ABORTED -> Messages.NoticeOccasion_aborted();
      case FAILURE -> Messages.NoticeOccasion_failure();
      case SUCCESS -> Messages.NoticeOccasion_success();
      case UNSTABLE -> Messages.NoticeOccasion_unstable();
      case NOT_BUILT -> Messages.NoticeOccasion_not_built();
    };
  }
}
