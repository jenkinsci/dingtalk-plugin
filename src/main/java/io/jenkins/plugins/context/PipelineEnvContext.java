package io.jenkins.plugins.context;

import hudson.EnvVars;
import hudson.model.Run;

/**
 * Collects the environment variables defined while a pipeline runs, so that they are available
 * when the notification content is rendered at the end of the build.
 *
 * <p>The variables are held on the build itself, in a {@link PipelineEnvAction}, so their lifetime
 * matches the build's and concurrent builds cannot observe each other's values.
 */
public class PipelineEnvContext {

	public static void merge(Run<?, ?> run, EnvVars value) {
		if (run == null || value == null) {
			return;
		}
		actionFor(run).merge(value);
	}

	/**
	 * Returns the cache attached to the build, creating it on demand. {@link Run#save()} locks on
	 * the build too, and a build is saved repeatedly while it runs, so contend for that lock only
	 * when the cache still has to be created.
	 */
	private static PipelineEnvAction actionFor(Run<?, ?> run) {
		PipelineEnvAction action = run.getAction(PipelineEnvAction.class);
		if (action != null) {
			return action;
		}
		synchronized (run) {
			action = run.getAction(PipelineEnvAction.class);
			if (action == null) {
				action = new PipelineEnvAction();
				run.addAction(action);
			}
			return action;
		}
	}

	public static EnvVars get(Run<?, ?> run) {
		PipelineEnvAction action = run == null ? null : run.getAction(PipelineEnvAction.class);
		return action == null ? new EnvVars() : action.snapshot();
	}

	/**
	 * Drops the cache once the notification has been sent. The build record is saved after
	 * {@code onCompleted} has run, so removing the action here also keeps it out of the saved build.
	 */
	public static void reset(Run<?, ?> run) {
		if (run == null) {
			return;
		}
		synchronized (run) {
			PipelineEnvAction action = run.getAction(PipelineEnvAction.class);
			if (action != null) {
				run.removeAction(action);
			}
		}
	}
}
