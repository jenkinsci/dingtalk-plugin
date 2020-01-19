package io.jenkins.plugins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import java.io.IOException;
import java.util.Collection;
import javax.annotation.Nonnull;
import jenkins.tasks.SimpleBuildStep;

/**
 * @author liuwei
 * @date 2020/1/19 21:03
 */
@Extension
public class DingTalkBuilder extends Builder implements SimpleBuildStep {

  @Override
  public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
      @Nonnull Launcher launcher, @Nonnull TaskListener listener)
      throws InterruptedException, IOException {

  }


}
