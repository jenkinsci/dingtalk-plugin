package com.ztbsuper.dingding;


import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by Marvin on 16/8/25.
 */
public class DingdingNotifier extends Notifier {

    private String accessToken;

    private String title;

    private String message;

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private boolean onAbort;

    public String getJenkinsURL() {
        return jenkinsURL;
    }

    private String jenkinsURL;

    public boolean isOnStart() {
        return onStart;
    }

    public boolean isOnSuccess() {
        return onSuccess;
    }

    public boolean isOnFailed() {
        return onFailed;
    }

    public boolean onAbort() {
        return onAbort;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    @DataBoundConstructor
    public DingdingNotifier(String accessToken, String title, String message, boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort, String jenkinsURL) {
        super();
        this.accessToken = accessToken;
        this.message = message;
        this.title = title;
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort = onAbort;
        this.jenkinsURL = jenkinsURL;
    }

    public DingdingService newDingdingService(AbstractBuild build, TaskListener listener) {
        return new DingdingServiceImpl(jenkinsURL, accessToken, title, message, onStart, onSuccess, onFailed, onAbort, listener, build);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }


    @Override
    public DingdingNotifierDescriptor getDescriptor() {
        return (DingdingNotifierDescriptor) super.getDescriptor();
    }

    @Extension
    public static class DingdingNotifierDescriptor extends BuildStepDescriptor<Publisher> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "钉钉通知器配置";
        }

        public String getDefaultURL() {
            Jenkins instance = Jenkins.getInstance();
            assert instance != null;
            if(instance.getRootUrl() != null){
                return instance.getRootUrl();
            }else{
                return "";
            }
        }

    }
}
