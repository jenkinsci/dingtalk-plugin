package io.jenkins.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;
import jenkins.model.GlobalConfiguration;
import lombok.Getter;
import lombok.ToString;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author liuwei
 * @date 2019/12/19 09:40
 * @desc 钉钉机器人通知
 */
@Getter
@ToString
@SuppressWarnings("unused")
public class DingTalkNotifier extends Notifier {

  private Set<String> robots;

  @DataBoundSetter
  public void setRobots(Set<String> robots) {
    this.robots = robots;
  }

  @DataBoundConstructor
  public DingTalkNotifier(Set<String> robots) {
    this.robots = robots;
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
      return Messages.displayName();
    }


    /**
     * 默认的机器人
     *
     * @return Set<String>
     */
    public Set<String> getDefaultRobots() {
      return Collections.emptySet();
    }

    /**
     * 选择 `机器人` 下拉框
     *
     * @return ListBoxModel
     */
    public CopyOnWriteArrayList<DingTalkRobotConfig> getRobotConfigs() {
      return Objects.requireNonNull(
          GlobalConfiguration.all().get(DingTalkGlobalConfig.class))
          .getRobotConfigs();
    }
  }
}
