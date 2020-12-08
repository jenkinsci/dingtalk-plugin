package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.Secret;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 安全策略配置页面
 *
 * @author liuwei
 * @date 2019/12/25 17:09
 */
@Getter
@Setter
@ToString
public class DingTalkSecurityPolicyConfig implements Describable<DingTalkSecurityPolicyConfig> {

  private String type;

  private String desc;

  private Secret value;

  @DataBoundConstructor
  public DingTalkSecurityPolicyConfig(String type, String value, String desc) {
    this.type = type;
    this.desc = desc;
    this.value = Secret.fromString(value);
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
    return Arrays.stream(
            // 兼容 2.3.2 版本之前的数据
            value.replaceAll("\n", ",").split(","))
        .collect(Collectors.toSet());
  }

  public void setValue(String value) {
    this.value = Secret.fromString(value);
  }

  @Override
  public Descriptor<DingTalkSecurityPolicyConfig> getDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkSecurityPolicyConfigDescriptor.class);
  }

  public static DingTalkSecurityPolicyConfig of(SecurityPolicyEnum securityPolicyEnum) {
    return new DingTalkSecurityPolicyConfig(
        securityPolicyEnum.name(), "", securityPolicyEnum.getDesc());
  }

  @Extension
  public static class DingTalkSecurityPolicyConfigDescriptor
      extends Descriptor<DingTalkSecurityPolicyConfig> {}
}
