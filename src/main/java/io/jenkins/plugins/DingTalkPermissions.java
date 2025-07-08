package io.jenkins.plugins;

import hudson.security.Permission;
import hudson.security.PermissionGroup;
import hudson.security.PermissionScope;

/**
 * DingTalk plugin permission definitions
 *
 * @author liuwei
 */
public class DingTalkPermissions {

    /**
     * DingTalk permission group
     */
    public static final PermissionGroup GROUP = new PermissionGroup(
            DingTalkPermissions.class,
            Messages._DingTalkPermissions_GroupTitle()
    );

    /**
     * Permission to configure DingTalk - allows users to view, modify, test DingTalk configurations and manage robots
     */
    public static final Permission CONFIGURE = new Permission(
            GROUP,
            "Configure",
            Messages._DingTalkPermissions_Configure_Description(),
            jenkins.model.Jenkins.ADMINISTER,
            PermissionScope.JENKINS
    );
}
