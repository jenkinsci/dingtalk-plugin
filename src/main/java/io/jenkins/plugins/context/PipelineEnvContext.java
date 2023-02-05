package io.jenkins.plugins.context;

import hudson.EnvVars;

public class PipelineEnvContext {

  private final static ThreadLocal<EnvVars> store = new ThreadLocal<>();

  public static void merge(EnvVars value) {
    EnvVars current = store.get();

    if (current == null) {
      store.set(value);
    } else {
      current.overrideAll(value);
    }
  }

  public static EnvVars get() {
    return store.get();
  }

}
