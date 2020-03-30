package io.jenkins.plugins.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author liuwei
 * @date 2020/3/28 15:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ButtonModel extends AbstractDescribableImpl<ButtonModel> {

  private String title;

  private String actionUrl;

  @DataBoundConstructor
  public ButtonModel(String title, String actionUrl) {
    this.title = title;
    this.actionUrl = actionUrl;
  }

  public static ButtonModel of(String title, String actionUrl) {
    return new ButtonModel(title, actionUrl);
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<ButtonModel> {

  }
}
