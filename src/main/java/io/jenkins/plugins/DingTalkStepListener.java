package io.jenkins.plugins;

import hudson.EnvVars;
import hudson.Extension;
import io.jenkins.plugins.context.PipelineEnvContext;
import lombok.extern.slf4j.Slf4j;
import org.jenkinsci.plugins.workflow.flow.StepListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.springframework.lang.NonNull;

@Slf4j
@Extension(optional = true)
public class DingTalkStepListener implements StepListener {
  @Override
  public void notifyOfNewStep(@NonNull Step step, @NonNull StepContext context) {
    try {
      EnvVars vars = context.get(EnvVars.class);
      PipelineEnvContext.merge(vars);
    } catch (Exception e) {
      log.error("钉钉插件在获取 pipeline 中的环境变量时异常", e);
    }
  }
}
