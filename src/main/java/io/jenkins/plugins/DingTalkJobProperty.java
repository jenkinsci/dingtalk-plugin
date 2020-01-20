package io.jenkins.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import io.jenkins.plugins.DingTalkNotifierConfig.DingTalkNotifierConfigDescriptor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import jenkins.YesNoMaybe;
import jenkins.model.Jenkins;
import lombok.ToString;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 任务配置页面添加钉钉配置
 *
 * @author liuwei
 * @date 2020/1/20 16:56
 */
@ToString
public class DingTalkJobProperty extends JobProperty<Job<?,?>> {

  private CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigs;

  /**
   * 在配置页面展示的列表，需要跟 `全局配置` 同步机器人信息
   *
   * @return CopyOnWriteArrayList<DingTalkNotifierConfig>
   */
  public CopyOnWriteArrayList<DingTalkNotifierConfig> getNotifierConfigs() {

    CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigsList = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<DingTalkRobotConfig> robotConfigs = DingTalkGlobalConfig.getInstance()
        .getRobotConfigs();

    for (DingTalkRobotConfig robotConfig : robotConfigs) {
      String id = robotConfig.getId();
      DingTalkNotifierConfig newNotifierConfig = new DingTalkNotifierConfig(robotConfig);
      if (notifierConfigs != null && !notifierConfigs.isEmpty()) {
        for (DingTalkNotifierConfig notifierConfig : notifierConfigs) {
          String robotId = notifierConfig.getRobotId();
          if (id.equals(robotId) && notifierConfig.isChecked()) {
            newNotifierConfig.setChecked(true);
            newNotifierConfig.setAtMobile(notifierConfig.getAtMobile());
          }
        }
      }
      notifierConfigsList.add(newNotifierConfig);
    }

    return notifierConfigsList;
  }

  /**
   * 获取用户设置的通知配置
   *
   * @return List<DingTalkNotifierConfig>
   */
  public List<DingTalkNotifierConfig> getCheckedNotifierConfigs() {
    CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigs = this.getNotifierConfigs();

    return notifierConfigs.stream()
        .filter(DingTalkNotifierConfig::isChecked)
        .collect(Collectors.toList());
  }

  @DataBoundConstructor
  public DingTalkJobProperty(
      CopyOnWriteArrayList<DingTalkNotifierConfig> notifierConfigs) {
    this.notifierConfigs = notifierConfigs;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
    return true;
  }

  @Extension(ordinal = -99999)
  public static class DingTalkJobPropertyDescriptor extends JobPropertyDescriptor {

    @Override
    public boolean isApplicable(Class<? extends Job> jobType) {
      return true;
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