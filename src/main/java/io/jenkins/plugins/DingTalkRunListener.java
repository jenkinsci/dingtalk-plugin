package io.jenkins.plugins;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Cause;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UpstreamCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.model.listeners.RunListener;
import hudson.scm.ChangeLogSet;
import io.jenkins.plugins.context.PipelineEnvContext;
import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.enums.MsgTypeEnum;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import io.jenkins.plugins.model.BuildExecutor;
import io.jenkins.plugins.model.BuildJobModel;
import io.jenkins.plugins.model.ButtonModel;
import io.jenkins.plugins.model.MessageModel;
import io.jenkins.plugins.service.DingTalkService;
import io.jenkins.plugins.tools.DingTalkUtils;
import io.jenkins.plugins.tools.Logger;
import io.jenkins.plugins.tools.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import jenkins.scm.RunWithSCM;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 所有项目触发
 *
 * @author liuwei
 */
@Log4j
@Extension
public class DingTalkRunListener extends RunListener<Run<?, ?>> {

	@Override
	public void onStarted(Run<?, ?> run, TaskListener listener) {
		DingTalkGlobalConfig globalConfig = DingTalkGlobalConfig.getInstance();
		DingTalkUtils.log(listener, "全局配置信息，%s", Utils.toJson(globalConfig));
		this.send(run, listener, NoticeOccasionEnum.START);
	}

	@Override
	public void onCompleted(Run<?, ?> run, @NonNull TaskListener listener) {
		Result result = run.getResult();
		NoticeOccasionEnum noticeOccasion = getNoticeOccasion(result);
		try {
			this.send(run, listener, noticeOccasion);
		} catch (Exception e) {
			e.printStackTrace();
			DingTalkUtils.log(listener, "发送消息时报错: %s", e);
		} finally {
			// 重置环境变量
			PipelineEnvContext.reset(run);
		}
	}

	private NoticeOccasionEnum getNoticeOccasion(Result result) {
		if (Result.SUCCESS.equals(result)) {
			return NoticeOccasionEnum.SUCCESS;
		}
		if (Result.FAILURE.equals(result)) {
			return NoticeOccasionEnum.FAILURE;
		}
		if (Result.ABORTED.equals(result)) {
			return NoticeOccasionEnum.ABORTED;
		}
		if (Result.UNSTABLE.equals(result)) {
			return NoticeOccasionEnum.UNSTABLE;
		}
		if (Result.NOT_BUILT.equals(result)) {
			return NoticeOccasionEnum.NOT_BUILT;
		}
		return null;
	}

	private BuildStatusEnum getBuildStatus(NoticeOccasionEnum noticeOccasion) {
		return switch (noticeOccasion) {
			case START -> BuildStatusEnum.START;
			case SUCCESS -> BuildStatusEnum.SUCCESS;
			case FAILURE -> BuildStatusEnum.FAILURE;
			case ABORTED -> BuildStatusEnum.ABORTED;
			case UNSTABLE -> BuildStatusEnum.UNSTABLE;
			case NOT_BUILT -> BuildStatusEnum.NOT_BUILT;
		};
	}

	private BuildExecutor getExecutorFromUser(Run<?, ?> run, TaskListener listener) {
		UserIdCause userIdCause = run.getCause(UserIdCause.class);

		if (userIdCause != null && userIdCause.getUserId() != null) {
			User user = User.getById(userIdCause.getUserId(), false);
			if (user != null) {
				String name = user.getDisplayName();
				String mobile = mobileOf(user);
				if (StringUtils.isEmpty(mobile)) {
					DingTalkUtils.log(
							listener,
							"用户【%s】暂未设置手机号码，请前往 %s 添加。",
							name,
							user.getAbsoluteUrl() + "/configure"
					);
				}
				return new BuildExecutor(name, mobile);
			}
		}

		return null;
	}

	/**
	 * Reads the mobile number a user configured for DingTalk.
	 *
	 * <p>{@link User#getProperty} is documented to return null: a user only carries the properties
	 * that were registered when the user was loaded, and nothing attaches them afterwards. A user
	 * without this property simply has no mobile number configured, which the caller already
	 * handles.
	 */
	static String mobileOf(User user) {
		DingTalkUserProperty property = user.getProperty(DingTalkUserProperty.class);
		return property == null ? null : property.getMobile();
	}

	private BuildExecutor getExecutorFromRemote(Run<?, ?> run) {
		RemoteCause remoteCause = run.getCause(RemoteCause.class);

		if (remoteCause != null) {
			return new BuildExecutor(
					String.format("%s %s", remoteCause.getAddr(), remoteCause.getNote()),
					null
			);
		}

		return null;
	}

	private BuildExecutor getExecutorFromUpstream(Run<?, ?> run, TaskListener listener) {
		UpstreamCause upstreamCause = run.getCause(UpstreamCause.class);
		if (upstreamCause != null) {
			Job<?, ?> job = Jenkins.get()
					.getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
			if (job != null) {
				Run<?, ?> upstream = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
				if (upstream != null) {
					return this.getExecutor(upstream, listener);
				}
			}
			return new BuildExecutor(upstreamCause.getUpstreamProject(), null);
		}
		return null;
	}

	private BuildExecutor getExecutorFromBuild(Run<?, ?> run) {
		return new BuildExecutor(
				run.getCauses()
						.stream()
						.map(Cause::getShortDescription)
						.collect(Collectors.joining()),
				null
		);
	}

	/**
	 * @see <a
	 * href="https://github.com/jenkinsci/build-user-vars-plugin/blob/master/src/main/java/org/jenkinsci/plugins/builduser/BuildUser.java">...</a>
	 */
	private BuildExecutor getExecutor(Run<?, ?> run, TaskListener listener) {
		BuildExecutor executor = getExecutorFromUser(run, listener);
		if (executor == null) {
			executor = getExecutorFromRemote(run);
		}
		if (executor == null) {
			executor = getExecutorFromUpstream(run, listener);
		}
		if (executor == null) {
			executor = getExecutorFromBuild(run);
		}
		return executor;
	}


	private EnvVars getEnvVars(Run<?, ?> run, TaskListener listener) {
		EnvVars jobEnvVars;
		try {
			jobEnvVars = run.getEnvironment(listener);
		} catch (Exception e) {
			jobEnvVars = new EnvVars();
			log.error(e);
			DingTalkUtils.log(listener, "获取 job 任务的环境变量时发生异常");
			DingTalkUtils.log(listener, ExceptionUtils.getStackTrace(e));
			Thread.currentThread().interrupt();
		}
		try {
			EnvVars pipelineEnvVars = PipelineEnvContext.get(run);
			jobEnvVars.overrideAll(pipelineEnvVars);
		} catch (Exception e) {
			log.error(e);
			DingTalkUtils.log(listener, "获取 pipeline 环境变量时发生异常");
			DingTalkUtils.log(listener, ExceptionUtils.getStackTrace(e));
		}
		return jobEnvVars;
	}

	private boolean skip(TaskListener listener, NoticeOccasionEnum noticeOccasion,
			DingTalkNotifierConfig notifierConfig) {
		String stage = noticeOccasion.name();
		Set<String> noticeOccasions = notifierConfig.getNoticeOccasions();
		if (noticeOccasions.contains(stage)) {
			return false;
		}
		DingTalkUtils.log(listener, "机器人 %s 已跳过 %s 环节", notifierConfig.getRobotName(), stage);
		return true;
	}


	/** The last entry of the build's changelog; null before checkout or when nothing changed. */
	static ChangeLogSet.Entry latestChangeOf(Run<?, ?> run) {
		if (!(run instanceof RunWithSCM)) {
			return null;
		}
		ChangeLogSet.Entry latest = null;
		for (ChangeLogSet<? extends ChangeLogSet.Entry> changeSet : ((RunWithSCM<?, ?>) run).getChangeSets()) {
			for (ChangeLogSet.Entry entry : changeSet) {
				latest = entry;
			}
		}
		return latest;
	}

	/** The first line of the commit message; git reports only that, Subversion the whole text. */
	static String titleOf(ChangeLogSet.Entry change) {
		return StringUtils.substringBefore(StringUtils.defaultString(change.getMsg()), "\n").trim();
	}

	/** {@code <short id> <title>（<author>）}, without the id when the SCM has none. */
	static String summaryOf(ChangeLogSet.Entry change) {
		String id = change.getCommitId();
		String prefix = StringUtils.isEmpty(id) ? "" : StringUtils.left(id, 7) + " ";
		return prefix + titleOf(change) + "（" + change.getAuthor().getDisplayName() + "）";
	}

	private void send(Run<?, ?> run, TaskListener listener, NoticeOccasionEnum noticeOccasion) {
		Job<?, ?> job = run.getParent();
		DingTalkJobProperty property = job.getProperty(DingTalkJobProperty.class);

		if (property == null) {
			DingTalkUtils.log(listener, "当前任务未配置机器人，已跳过");
			return;
		}

		// 环境变量
		EnvVars envVars = getEnvVars(run, listener);

		// 执行人信息
		BuildExecutor executor = getExecutor(run, listener);
		String executorName = envVars.get("EXECUTOR_NAME", executor.getName());
		String executorMobile = envVars.get("EXECUTOR_MOBILE", executor.getMobile());

		// 项目信息
		String projectName = job.getFullDisplayName();
		String projectUrl = job.getUrl();
        try {
            projectUrl = job.getAbsoluteUrl();
        } catch (IllegalStateException e) {
            DingTalkUtils.log(
                    listener,
                    "Get Project URL error, %s "
                            + "Please set jenkins Root URL in [ System Configuration >> System >> Jenkins Location >> Jenkins URL ]",
                    e.toString());
        }

		// 构建信息
		String jobName = run.getDisplayName();
        String jobUrl = "";
        try {
            jobUrl = run.getAbsoluteUrl();
        } catch (IllegalStateException e) {
            DingTalkUtils.log(
                    listener,
                    "Get job URL error, %s "
                            + "Please set jenkins Root URL in [ System Configuration >> System >> Jenkins Location >> Jenkins URL ]",
                    e.toString());
        }

		String duration = run.getDurationString();
		BuildStatusEnum statusType = getBuildStatus(noticeOccasion);

		// 设置环境变量
		envVars.put("EXECUTOR_NAME", executorName == null ? "" : executorName);
		envVars.put("EXECUTOR_MOBILE", executorMobile == null ? "" : executorMobile);
		envVars.put("PROJECT_NAME", projectName);
		envVars.put("PROJECT_URL", projectUrl);
		envVars.put("JOB_NAME", jobName);
		envVars.put("JOB_URL", jobUrl);
		envVars.put("JOB_DURATION", duration);
		envVars.put("JOB_STATUS", statusType.getLabel());
		ChangeLogSet.Entry change = latestChangeOf(run);
		envVars.put("COMMIT_ID", change == null ? "" : StringUtils.defaultString(change.getCommitId()));
		envVars.put("COMMIT_TITLE", change == null ? "" : titleOf(change));
		envVars.put("COMMIT_AUTHOR", change == null ? "" : change.getAuthor().getDisplayName());

		List<ButtonModel> btns = Utils.createDefaultBtns(jobUrl);
		List<String> result = new ArrayList<>();
		List<DingTalkNotifierConfig> notifierConfigs = property.getAvailableNotifierConfigs();

		for (DingTalkNotifierConfig item : notifierConfigs) {
			boolean skipped = skip(listener, noticeOccasion, item);

			if (skipped) {
				continue;
			}

			String robotId = item.getRobotId();
			String content = item.getContent();
			String rawMessage = item.getMessage();
			boolean atAll = item.isAtAll();
			Set<String> atMobiles = item.resolveAtMobiles(envVars);

			if (StringUtils.isNotEmpty(executorMobile)) {
				atMobiles.add(executorMobile);
			}

            String title = String.format("%s %s", projectName, statusType.getLabel());

            MessageModel msgModel = item.isRaw()
                    ? MessageModel.builder()
                            .type(MsgTypeEnum.MARKDOWN)
                            .atAll(atAll)
                            .atMobiles(atMobiles)
                            .title(title)
                            .text(envVars.expand(rawMessage).replace("\\\\n", "\n"))
                            .build()
                    : MessageModel.builder()
                            .type(MsgTypeEnum.ACTION_CARD)
                            .atAll(atAll)
                            .atMobiles(atMobiles)
                            .title(title)
                            .text(BuildJobModel.builder()
                                    .projectName(projectName)
                                    .projectUrl(projectUrl)
                                    .jobName(jobName)
                                    .jobUrl(jobUrl)
                                    .statusType(statusType)
                                    .duration(duration)
                                    .executorName(executorName)
                                    .executorMobile(executorMobile)
                                    .change(change == null ? "" : summaryOf(change))
                                    .content(envVars.expand(content).replace("\\\\n", "\n"))
                                    .build()
                                    .toMarkdown())
                            .btns(btns)
                            .build();

			DingTalkUtils.log(listener, "当前机器人信息，%s", Utils.toJson(item));
			DingTalkUtils.log(listener, "发送的消息详情，%s", Utils.toJson(msgModel));

			String msg = DingTalkService.getInstance().send(robotId, msgModel);

			if (msg != null) {
				result.add(msg);
			}
		}

		if (!result.isEmpty()) {
			result.forEach(msg -> Logger.error(listener, msg));
		}
	}
}
