package io.jenkins.plugins;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import io.jenkins.plugins.model.NoticeOccasionOption;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 */
@Getter
@Setter
@ToString
@Slf4j
public class DingTalkNotifierConfig extends AbstractDescribableImpl<DingTalkNotifierConfig> {

	private boolean raw;
	private boolean disabled;
	private boolean checked;

	private String robotId;

	private String robotName;

	private boolean atAll;

	private String atMobile;

	private String content;
	private String message;

	private Set<String> noticeOccasions;

	private static Set<String> getDefaultNoticeOccasions() {
		return DingTalkGlobalConfig.getInstance().getNoticeOccasions();
	}

	public Set<String> getNoticeOccasions() {
		return noticeOccasions == null ? getDefaultNoticeOccasions() : noticeOccasions;
	}

	public Set<String> resolveAtMobiles(EnvVars envVars) {
		if (StringUtils.isEmpty(atMobile)) {
			return new HashSet<>(16);
		}
		String realMobile = envVars.expand(atMobile);
		return Arrays.stream(
						StringUtils.split(
								// 支持多行，一行支持多个手机号
								realMobile.replace("\n", ","),
								","
						)
				)
				.collect(Collectors.toSet());
	}

	public String getContent() {
		return content == null ? "" : content;
	}

	@DataBoundConstructor
	public DingTalkNotifierConfig(
			boolean raw,
			boolean disabled,
			boolean checked,
			String robotId,
			String robotName,
			boolean atAll,
			String atMobile,
			String content,
			String message,
			Set<String> noticeOccasions) {
		this.raw = raw;
		this.disabled = disabled;
		this.checked = checked;
		this.robotId = robotId;
		this.robotName = robotName;
		this.atAll = atAll;
		this.atMobile = atMobile;
		this.content = content;
		this.message = message;
		this.noticeOccasions = noticeOccasions;
	}

	public DingTalkNotifierConfig(DingTalkRobotConfig robotConfig) {
		this(
				false,
				false,
				false,
				robotConfig.getId(),
				robotConfig.getName(),
				false,
				null,
				null,
				null,
				getDefaultNoticeOccasions()
		);
	}

	public void copy(DingTalkNotifierConfig notifierConfig) {
		try {
			BeanUtils.copyProperties(this, notifierConfig);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("读取机器人配置失败", e);
			throw new RuntimeException(e);
		}
	}

	@Extension
	public static class DingTalkNotifierConfigDescriptor extends Descriptor<DingTalkNotifierConfig> {

		/**
		 * 通知时机列表
		 *
		 * @return 通知时机
		 */
    public List<List<NoticeOccasionOption>> getNoticeOccasionRows() {
      return DingTalkGlobalConfig.buildNoticeOccasionRows();
    }
  }
}
