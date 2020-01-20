package io.jenkins.plugins;

import hudson.Extension;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionListener;

/**
 * @author liuwei
 * @date 2020/1/19 21:03
 */
@Extension
public class DingTalkFlowExecutionListener extends FlowExecutionListener {

  @Override
  public void onCreated(@Nonnull FlowExecution execution) {
    System.out.println("==================== onCreated ========================");
    super.onCreated(execution);
  }

  @Override
  public void onRunning(@Nonnull FlowExecution execution) {
    System.out.println("==================== onRunning ========================");
    super.onRunning(execution);
  }

  @Override
  public void onResumed(@Nonnull FlowExecution execution) {
    System.out.println("==================== onResumed ========================");
    super.onResumed(execution);
  }

  @Override
  public void onCompleted(@Nonnull FlowExecution execution) {
    System.out.println("==================== onCompleted ========================");
    super.onCompleted(execution);
  }
}
