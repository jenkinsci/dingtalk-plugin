package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 */
@Getter
@Setter
@ToString
public class DingTalkNotifierConfig extends AbstractDescribableImpl<DingTalkNotifierConfig> {

  private boolean checked;

  private String robotId;

  private String robotName;

  private boolean atAll;

  private String atMobile;

  private String content;

  private Set<String> noticeOccasions;

  private static Set<String> getDefaultNoticeOccasions() {
    return DingTalkGlobalConfig.getInstance().getNoticeOccasions();
  }

  public Set<String> getNoticeOccasions() {
    return noticeOccasions == null ? getDefaultNoticeOccasions() : noticeOccasions;
  }

  public Set<String> getAtMobiles() {
    if (StringUtils.isEmpty(atMobile)) {
      return new HashSet<>(16);
    }
    return Arrays.stream(StringUtils.split(atMobile.replace("\n", ","), ","))
        .collect(Collectors.toSet());
  }

  public String getContent() {
    return content == null ? "" : content;
  }

  @DataBoundConstructor
  public DingTalkNotifierConfig(
      boolean checked,
      String robotId,
      String robotName,
      boolean atAll,
      String atMobile,
      String content,
      Set<String> noticeOccasions) {
    this.checked = checked;
    this.robotId = robotId;
    this.robotName = robotName;
    this.atAll = atAll;
    this.atMobile = atMobile;
    this.content = content;
    this.noticeOccasions = noticeOccasions;
  }

  public DingTalkNotifierConfig(DingTalkRobotConfig robotConfig) {
    this(
        false,
        robotConfig.getId(),
        robotConfig.getName(),
        false,
        null,
        null,
        getDefaultNoticeOccasions());
  }

  public void copy(DingTalkNotifierConfig notifierConfig) {
    this.setChecked(notifierConfig.isChecked());
    this.setAtAll(notifierConfig.isAtAll());
    this.setAtMobile(notifierConfig.getAtMobile());
    this.setContent(notifierConfig.getContent());
    this.setNoticeOccasions(notifierConfig.getNoticeOccasions());
  }

  @Extension
  public static class DingTalkNotifierConfigDescriptor extends Descriptor<DingTalkNotifierConfig> {
    /**
     * 通知时机列表
     *
     * @return 通知时机
     */
    public NoticeOccasionEnum[] getNoticeOccasionTypes() {
      return NoticeOccasionEnum.values();
    }
  }
}
