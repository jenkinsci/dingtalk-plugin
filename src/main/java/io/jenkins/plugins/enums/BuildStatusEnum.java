package io.jenkins.plugins.enums;

import io.jenkins.plugins.Messages;
import io.jenkins.plugins.tools.Constants;
import lombok.Getter;

/**
 * Build status enumeration
 */
@Getter
public enum BuildStatusEnum {

    START(Messages.BuildStatusType_start(), Constants.COLOR_GEEK_BLUE),
    FAILURE(Messages.BuildStatusType_failure(), Constants.COLOR_RED),
    SUCCESS(Messages.BuildStatusType_success(), Constants.COLOR_GREEN),
    ABORTED(Messages.BuildStatusType_aborted(), Constants.COLOR_CYAN),
    UNSTABLE(Messages.BuildStatusType_unstable(), Constants.COLOR_CYAN),
    NOT_BUILT(Messages.BuildStatusType_not_built(), Constants.COLOR_CYAN),
    UNKNOWN(Messages.BuildStatusType_unknown(), Constants.COLOR_PURPLE);

    private final String label;
    private final String color;

    BuildStatusEnum(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
