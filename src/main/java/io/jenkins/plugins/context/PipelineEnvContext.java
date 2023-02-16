package io.jenkins.plugins.context;

import hudson.EnvVars;

public class PipelineEnvContext {

	private static final ThreadLocal<EnvVars> store = new ThreadLocal<>();

	public static void merge(EnvVars value) {
		if (value == null) {
			return;
		}
		EnvVars current = store.get();
		if (current == null) {
			store.set(value);
		} else {
			current.overrideAll(value);
		}
	}

	public static EnvVars get() {
		EnvVars current = store.get();
		return current == null ? new EnvVars() : current;
	}

	public static void reset() {
		store.remove();
	}
}
