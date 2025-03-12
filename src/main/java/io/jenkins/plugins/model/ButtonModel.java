package io.jenkins.plugins.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
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
