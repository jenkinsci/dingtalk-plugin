package io.jenkins.plugins.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.EnvVars;
import hudson.model.Action;
import hudson.model.Run;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PipelineEnvContextTest {

  /**
   * A stand-in for a real {@link Run} that keeps an action list, like {@code Actionable} does.
   */
  private static Run<?, ?> fakeRun() {
    Run<?, ?> run = mock(Run.class);
    List<Action> actions = new ArrayList<>();
    doAnswer(invocation -> actions.add(invocation.getArgument(0)))
        .when(run)
        .addAction(any(Action.class));
    doAnswer(invocation -> actions.remove(invocation.<Action>getArgument(0)))
        .when(run)
        .removeAction(any(Action.class));
    when(run.getAction(PipelineEnvAction.class))
        .thenAnswer(
            invocation ->
                actions.stream()
                    .filter(PipelineEnvAction.class::isInstance)
                    .findFirst()
                    .orElse(null));
    return run;
  }

  @Test
  void concurrentRunsAreIsolatedByBuild() {
    // Reproduces #366: two concurrent builds using the same variable name must not overwrite
    // each other.
    Run<?, ?> a = fakeRun();
    Run<?, ?> b = fakeRun();

    PipelineEnvContext.merge(a, new EnvVars("FOO", "value-a"));
    PipelineEnvContext.merge(b, new EnvVars("FOO", "value-b"));

    assertEquals("value-a", PipelineEnvContext.get(a).get("FOO"));
    assertEquals("value-b", PipelineEnvContext.get(b).get("FOO"));
  }

  @Test
  void mergeAccumulatesWithinSameBuild() {
    Run<?, ?> run = fakeRun();
    PipelineEnvContext.merge(run, new EnvVars("A", "1"));
    PipelineEnvContext.merge(run, new EnvVars("B", "2"));

    EnvVars merged = PipelineEnvContext.get(run);
    assertEquals("1", merged.get("A"));
    assertEquals("2", merged.get("B"));
  }

  @Test
  void resetDetachesTheCacheFromTheBuild() {
    Run<?, ?> run = fakeRun();
    PipelineEnvContext.merge(run, new EnvVars("K", "v"));

    PipelineEnvContext.reset(run);

    // The action is detached, so it stays out of the build record and the cache is released.
    assertNull(run.getAction(PipelineEnvAction.class));
    assertTrue(PipelineEnvContext.get(run).isEmpty());
  }

  @Test
  void resetOnlyAffectsTargetBuild() {
    Run<?, ?> a = fakeRun();
    Run<?, ?> b = fakeRun();
    PipelineEnvContext.merge(a, new EnvVars("K", "a"));
    PipelineEnvContext.merge(b, new EnvVars("K", "b"));

    PipelineEnvContext.reset(a);

    assertTrue(PipelineEnvContext.get(a).isEmpty());
    assertEquals("b", PipelineEnvContext.get(b).get("K"));
  }

  @Test
  void getReturnsDefensiveCopy() {
    Run<?, ?> run = fakeRun();
    PipelineEnvContext.merge(run, new EnvVars("K", "original"));

    PipelineEnvContext.get(run).put("K", "mutated");

    assertEquals("original", PipelineEnvContext.get(run).get("K"));
  }

  @Test
  void buildWithoutCachedEnvironmentYieldsEmpty() {
    assertTrue(PipelineEnvContext.get(fakeRun()).isEmpty());
  }

  @Test
  void nullRunIsHandledGracefully() {
    PipelineEnvContext.merge(null, new EnvVars("X", "1"));
    assertTrue(PipelineEnvContext.get(null).isEmpty());
    PipelineEnvContext.reset(null);
  }

  @Test
  void mergeIgnoresNullValue() {
    Run<?, ?> run = fakeRun();
    PipelineEnvContext.merge(run, null);
    assertTrue(PipelineEnvContext.get(run).isEmpty());
  }
}
