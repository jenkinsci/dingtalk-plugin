package io.jenkins.plugins.tools;

import hudson.model.TaskListener;
import io.jenkins.plugins.DingTalkGlobalConfig;

public class DingTalkUtils {

  public static void log(TaskListener listener, String formatMsg, Object... args) {
    DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
    boolean verbose = globalConfig.isVerbose();
    if (verbose) {
      // Logger.line(listener, LineType.START);
      Logger.debug(listener, "[钉钉插件]" + formatMsg, args);
      // Logger.line(listener, LineType.END);
    }
  }
}
