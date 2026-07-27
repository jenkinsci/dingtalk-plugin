package io.jenkins.plugins.context;

import hudson.EnvVars;
import hudson.model.InvisibleAction;

/**
 * In-memory cache of the pipeline environment collected for a single build.
 *
 * <p><strong>{@code vars} must stay {@code transient}.</strong> It accumulates the full environment
 * of every step, which can include credentials bound by {@code withCredentials}, and Jenkins saves
 * the build record on its own while the build runs. Were the field serializable, those credentials
 * would be written to {@code build.xml} in plain text. {@code PipelineEnvActionTest} pins this
 * down.
 */
class PipelineEnvAction extends InvisibleAction {

	private transient EnvVars vars;

	synchronized void merge(EnvVars value) {
		if (vars == null) {
			vars = new EnvVars();
		}
		vars.overrideAll(value);
	}

	synchronized EnvVars snapshot() {
		return vars == null ? new EnvVars() : new EnvVars(vars);
	}
}
