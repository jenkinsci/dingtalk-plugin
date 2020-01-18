package io.jenkins.plugins.enums;

import lombok.Getter;

/**
 * @author liuwei
 * @date 2019/12/19 11:28
 * @desc 通知时机
 */
public enum NoticeOccasionType {
  /**
   * 在启动构建时通知
   */
  START,

  /**
   * 构建中断时通知
   */
  CANCEL,

  /**
   * 构建失败时通知
   */
  FAILURE,

  /**
   * 构建不稳定时通知
   */
  UNSTABLE,

  /**
   * 在未构建时通知
   */
  NOT_BUILT,

  /**
   * 构建成功时通知
   */
  SUCCESS;

}
