package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import lombok.Getter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest2;

@Getter
public class DingTalkUserProperty extends UserProperty {

  private String mobile;

  public DingTalkUserProperty(String mobile) {
    this.mobile = mobile;
  }

  @Extension(ordinal = 1)
  public static final class DingTalkUserPropertyDescriptor extends UserPropertyDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return Messages.UserProperty_mobile();
    }

    @Override
    public UserProperty newInstance(User user) {
      return new DingTalkUserProperty(null);
    }

    @Override
    public UserProperty newInstance(@Nullable StaplerRequest2 req, @NonNull JSONObject formData) {
      return new DingTalkUserProperty(formData.optString("mobile"));
    }
  }
}
