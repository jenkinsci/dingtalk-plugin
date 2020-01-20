package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 * @date 2019/12/28 11:06
 */
@Getter
@Setter
@ToString
public class DingTalkNotifierConfig extends AbstractDescribableImpl<DingTalkNotifierConfig> {

  private boolean checked;

  private String robotId;

  private String robotName;

  private String atMobile;

  public Set<String> getAtMobiles() {
    if (StringUtils.isEmpty(atMobile)) {
      return null;
    }
    return Arrays.stream(StringUtils.split(atMobile, "\n")).collect(Collectors.toSet());
  }

  @DataBoundConstructor
  public DingTalkNotifierConfig(
      boolean checked,
      String robotId,
      String robotName,
      String atMobile
  ) {
    this.checked = checked;
    this.robotId = robotId;
    this.robotName = robotName;
    this.atMobile = atMobile;
  }

  public DingTalkNotifierConfig(DingTalkRobotConfig robotConfig) {
    this(false, robotConfig.getId(), robotConfig.getName(), null);
  }

  @Extension
  public static class DingTalkNotifierConfigDescriptor extends Descriptor<DingTalkNotifierConfig> {

  }
}
