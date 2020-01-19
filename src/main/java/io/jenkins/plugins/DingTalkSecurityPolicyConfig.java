package io.jenkins.plugins;

import hudson.util.Secret;
import io.jenkins.plugins.enums.SecurityPolicyType;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author liuwei
 * @date 2019/12/25 17:09
 * @desc 安全策略配置页面
 */
@Getter
@ToString
public class DingTalkSecurityPolicyConfig implements Describable<DingTalkSecurityPolicyConfig> {

  private boolean checked;

  private String type;

  private Secret value;

  private String desc;

  @DataBoundConstructor
  public DingTalkSecurityPolicyConfig(boolean checked, String type, String value, String desc) {
    this.checked = checked;
    this.type = type;
    this.value = Secret.fromString(value);
    this.desc = desc;
  }

  @DataBoundSetter
  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  @DataBoundSetter
  public void setType(String type) {
    this.type = type;
  }

  @DataBoundSetter
  public void setValue(String value) {
    this.value = Secret.fromString(value);
  }

  @DataBoundSetter
  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getValue() {
    if (value == null) {
      return null;
    }
    return value.getPlainText();
  }

  public Set<String> getValues() {
    String value = getValue();
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return Arrays.stream(value.split("\n")).collect(Collectors.toSet());
  }

  @Override
  public Descriptor<DingTalkSecurityPolicyConfig> getDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkSecurityPolicyConfigDescriptor.class);
  }

  public static DingTalkSecurityPolicyConfig of(SecurityPolicyType securityPolicyType) {
    return new DingTalkSecurityPolicyConfig(false, securityPolicyType.name(), "",
        securityPolicyType.getDesc());
  }

  @Extension
  public static class DingTalkSecurityPolicyConfigDescriptor extends
      Descriptor<DingTalkSecurityPolicyConfig> {

  }
}
