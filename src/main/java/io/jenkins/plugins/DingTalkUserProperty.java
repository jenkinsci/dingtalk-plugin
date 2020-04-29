package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

public class DingTalkUserProperty extends UserProperty {

  @Getter
  private String mobile;

  public DingTalkUserProperty(String mobile) {
    this.mobile = mobile;
  }

  @Extension(ordinal = 1)
  public static final class DingTalkUserPropertyDescriptor extends UserPropertyDescriptor {

    @Nonnull
    @Override
    public String getDisplayName() {
      return Messages.UserProperty_mobile();
    }

    @Override
    public UserProperty newInstance(User user) {
      return new DingTalkUserProperty(null);
    }

    @Override
    public UserProperty newInstance(@Nullable StaplerRequest req, @Nonnull JSONObject formData) {
      return new DingTalkUserProperty(formData.optString("mobile"));
    }
  }
}
