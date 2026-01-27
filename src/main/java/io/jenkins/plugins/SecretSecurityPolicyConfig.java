package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import io.jenkins.plugins.enums.SecurityPolicyEnum;
import lombok.ToString;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Sign security policy configuration.
 *
 * @author liuwei
 */
@ToString(callSuper = true)
public class SecretSecurityPolicyConfig extends DingTalkSecurityPolicyConfig {

  @DataBoundConstructor
  public SecretSecurityPolicyConfig(String value) {
    super(SecurityPolicyEnum.SECRET.name(), value, Messages.SecurityPolicyType_secret());
  }

  @Override
  public Descriptor<DingTalkSecurityPolicyConfig> getDescriptor() {
    return Jenkins.get().getDescriptorByType(DescriptorImpl.class);
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<DingTalkSecurityPolicyConfig> {
    @Override
    public String getDisplayName() {
      return Messages.SecurityPolicyType_secret();
    }
  }
}
