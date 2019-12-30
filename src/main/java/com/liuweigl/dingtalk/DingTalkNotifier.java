package com.liuweigl.dingtalk;

import com.liuweigl.dingtalk.DingTalkNotifierConfig.DingTalkNotifierConfigDescriptor;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import lombok.ToString;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 * @date 2019/12/19 09:40
 * @desc 钉钉机器人通知
 */
@ToString
@SuppressWarnings("unused")
public class DingTalkNotifier extends Notifier {

  private CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigs;

  /**
   * 需要跟 `全局配置` 同步机器人信息
   *
   * @return CopyOnWriteArrayList<DingTalkNotifierConfig>
   */
  public CopyOnWriteArrayList<DingTalkNotifierConfig> getNotifierConfigs() {
    if (notifierConfigs == null) {
      return null;
    }
    CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs = DingTalkGlobalConfig.getInstance()
        .getRobotConfigs();
    for (DingTalkNotifierConfig notifierConfig : notifierConfigs) {
      String robotId = notifierConfig.getRobotId();
      String robotName = notifierConfig.getRobotName();
      for (DingTalkRobotConfig robotConfig : robotConfigs) {
        String newRobotId = robotConfig.getId();
        String newName = robotConfig.getName();
        if (newRobotId.equals(robotId)) {
          if (!newName.equals(robotName)) {
            notifierConfig.setRobotName(newName);
          }
        }
      }
    }
    return notifierConfigs;
  }

  public List<DingTalkNotifierConfig> getCheckedNotifierConfigs() {
    CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigs = this.getNotifierConfigs();
    if (notifierConfigs == null) {
      return null;
    }
    return notifierConfigs.stream()
        .filter(DingTalkNotifierConfig::isChecked)
        .collect(Collectors.toList());
  }

  @DataBoundConstructor
  public DingTalkNotifier(
      CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigs) {
    this.notifierConfigs = notifierConfigs;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.NONE;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
    return true;
  }


  @Override
  public DingTalkNotifierDescriptor getDescriptor() {
    return (DingTalkNotifierDescriptor) super.getDescriptor();
  }


  @Extension
  public static class DingTalkNotifierDescriptor extends BuildStepDescriptor<Publisher> {

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      return true;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
      return "钉钉通知";
    }

    /**
     * 通知配置页面
     */
    public DingTalkNotifierConfigDescriptor getDingTalkNotifierConfigDescriptor() {
      return Jenkins.get().getDescriptorByType(DingTalkNotifierConfigDescriptor.class);
    }

    /**
     * 默认的配置项列表
     */
    public List<DingTalkNotifierConfig> getDefaultNotifierConfigs() {
      return DingTalkGlobalConfig
          .getInstance().getRobotConfigs()
          .stream()
          .map(DingTalkNotifierConfig::new)
          .collect(Collectors.toList());
    }

  }
}
