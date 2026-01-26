package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import lombok.ToString;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Keyword security policy configuration.
 *
 * @author liuwei
 */
@ToString(callSuper = true)
public class KeySecurityPolicyConfig extends DingTalkSecurityPolicyConfig {

  @DataBoundConstructor
  public KeySecurityPolicyConfig(String value) {
    super(SecurityPolicyEnum.KEY.name(), value, Messages.SecurityPolicyType_key());
  }

  @Override
  public Descriptor<DingTalkSecurityPolicyConfig> getDescriptor() {
    return Jenkins.get().getDescriptorByType(DescriptorImpl.class);
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<DingTalkSecurityPolicyConfig> {
    @Override
    public String getDisplayName() {
      return Messages.SecurityPolicyType_key();
    }
  }
}
