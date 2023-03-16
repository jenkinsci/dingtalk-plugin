package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 任务配置页面添加钉钉配置
 *
 * @author liuwei
 */
@ToString
@NoArgsConstructor
public class DingTalkJobProperty extends JobProperty<Job<?, ?>> {

	private ArrayList<DingTalkNotifierConfig> notifierConfigs;

	/**
	 * 在配置页面展示的列表，需要跟 `全局配置` 同步机器人信息
	 *
	 * @return 机器人配置列表
	 */
	public ArrayList<DingTalkNotifierConfig> getNotifierConfigs() {
		ArrayList<DingTalkNotifierConfig> notifierConfigList = new ArrayList<>();
		ArrayList<DingTalkRobotConfig> robotConfigs = DingTalkGlobalConfig.getInstance()
				.getRobotConfigs();

		for (DingTalkRobotConfig robotConfig : robotConfigs) {
			String id = robotConfig.getId();
			DingTalkNotifierConfig newNotifierConfig = new DingTalkNotifierConfig(robotConfig);

			if (this.notifierConfigs != null) {
				for (DingTalkNotifierConfig notifierConfig : this.notifierConfigs) {
					String robotId = notifierConfig.getRobotId();
					if (id.equals(robotId)) {
						newNotifierConfig.copy(notifierConfig);
					}
				}
			}

			notifierConfigList.add(newNotifierConfig);
		}

		return notifierConfigList;
	}

	/**
	 * 获取用户设置的通知配置
	 *
	 * @return 用户设置的通知配置
	 */
	public List<DingTalkNotifierConfig> getCheckedNotifierConfigs() {
		return this.getNotifierConfigs().stream()
				.filter(DingTalkNotifierConfig::isChecked)
				.collect(Collectors.toList());
	}

	public List<DingTalkNotifierConfig> getAvailableNotifierConfigs() {
		return this.getNotifierConfigs().stream()
				.filter(t -> t.isChecked() && !t.isDisabled())
				.collect(Collectors.toList());
	}

	@DataBoundConstructor
	public DingTalkJobProperty(ArrayList<DingTalkNotifierConfig> notifierConfigs) {
		this.notifierConfigs = notifierConfigs;
	}

	@Extension
	public static class DingTalkJobPropertyDescriptor extends JobPropertyDescriptor {

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return super.isApplicable(jobType);
		}

		/**
		 * 默认的配置项列表
		 *
		 * @return 默认的通知配置列表
		 */
		public List<DingTalkNotifierConfig> getDefaultNotifierConfigs() {
			return DingTalkGlobalConfig.getInstance()
					.getRobotConfigs()
					.stream()
					.map(DingTalkNotifierConfig::new)
					.collect(Collectors.toList());
		}
	}
}
