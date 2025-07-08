package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.ManagementLink;
import hudson.model.RootAction;
import hudson.security.Permission;
import hudson.util.FormApply;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.verb.POST;

@Extension()
public class DingTalkManagementLink extends ManagementLink implements RootAction {

  @Override
  public String getIconFileName() {
    // Only users with permission can see the management link
    if (!hasPermission()) {
      return null;
    }
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

  @NonNull
  @Override
  public Category getCategory() {
    return Category.CONFIGURATION;
  }

  @POST
  public void doConfigure(StaplerRequest2 req, StaplerResponse2 res)
      throws ServletException, FormException, IOException {
    // Check configuration permission
    checkPermission(DingTalkPermissions.CONFIGURE);
    getDingTalkGlobalConfigDescriptor().configure(req, req.getSubmittedForm());
    FormApply.success(req.getContextPath() + "/manage").generateResponse(req, res, null);
  }

  /**
   * Global configuration page
   *
   * @return DingTalkGlobalConfig
   */
  public Descriptor<DingTalkGlobalConfig> getDingTalkGlobalConfigDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkGlobalConfig.class);
  }

  /**
   * Check if the user has DingTalk configuration permission
   *
   * @return whether the user has permission
   */
  public boolean hasPermission() {
    return Jenkins.get().hasPermission(DingTalkPermissions.CONFIGURE);
  }

  /**
   * Check permission, throw exception if no permission
   *
   * @param permission the permission to check
   */
  public void checkPermission(Permission permission) {
    Jenkins.get().checkPermission(permission);
  }

  /**
   * Get the required permission for DingTalk configuration
   *
   * @return configuration permission
   */
  @Override
  public Permission getRequiredPermission() {
    return DingTalkPermissions.CONFIGURE;
  }

}
