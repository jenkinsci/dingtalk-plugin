package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import lombok.Getter;

/**
 * @author liuwei
 * @date 2019/12/19 11:28
 * @desc 通知时机
 */
public enum NoticeOccasionEnum {
  /**
   * 在启动构建时通知
   */
  START(Messages.NoticeOccasion_start()),

  /**
   * 构建中断时通知
   */
  ABORTED(Messages.NoticeOccasion_aborted()),

  /**
   * 构建失败时通知
   */
  FAILURE(Messages.NoticeOccasion_failure()),

  /**
   * 构建成功时通知
   */
  SUCCESS(Messages.NoticeOccasion_success()),

  /**
   * 构建不稳定时通知
   */
  UNSTABLE(Messages.NoticeOccasion_unstable()),

  /**
   * 在未构建时通知
   */
  NOT_BUILT(Messages.NoticeOccasion_not_built());

  @Getter
  private String desc;

  NoticeOccasionEnum(String desc) {
    this.desc = desc;
  }
}
