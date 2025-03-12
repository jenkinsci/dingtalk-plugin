package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.ManagementLink;
import hudson.util.FormApply;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.verb.POST;

@Extension(ordinal = Double.MAX_VALUE)
public class DingTalkManagementLink extends ManagementLink {

  @Override
  public String getIconFileName() {

    return "/plugin/dingding-notifications/images/dingtalk.png";
  }

  @Override
  public String getDisplayName() {
    return Messages.displayName();
  }

  @Override
  public String getUrlName() {
    return "dingtalk";
  }

  @Override
  public String getDescription() {
    return Messages.ManagementLink_description();
  }

  @POST
  public void doConfigure(StaplerRequest2 req, StaplerResponse2 res)
      throws ServletException, FormException, IOException {
    getDingTalkGlobalConfigDescriptor().configure(req, req.getSubmittedForm());
    FormApply.success(req.getContextPath() + "/manage").generateResponse(req, res, null);
  }

  /**
   * 全局配置页面
   *
   * @return DingTalkGlobalConfig
   */
  public Descriptor<DingTalkGlobalConfig> getDingTalkGlobalConfigDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkGlobalConfig.class);
  }

}
