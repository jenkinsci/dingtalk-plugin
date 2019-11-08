package com.ztbsuper.dingding;


import com.ztbsuper.DingDingSign;
import com.ztbsuper.KeyWord;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by Marvin on 16/8/25.
 */
public class DingdingNotifier extends Notifier {

    private String accessToken;

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

    private List<KeyWord> keywords = new ArrayList<>();

    private DingDingSign dingDingSign;

    private String ipAddress;

    public List<KeyWord> getKeywords() {
        return keywords;
    }

    public DingDingSign getDingDingSign() {
        return dingDingSign;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @DataBoundConstructor
    public DingdingNotifier(String accessToken, boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort, String jenkinsURL,DingDingSign dingDingSign,List<KeyWord> keywords) {
        super();
        this.accessToken = accessToken;
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort = onAbort;
        this.jenkinsURL = jenkinsURL;
        this.dingDingSign = dingDingSign;
        this.keywords = keywords;
    }

    public DingdingService newDingdingService(AbstractBuild build, TaskListener listener) {
        return new DingdingServiceImpl(jenkinsURL, accessToken, onStart, onSuccess, onFailed, onAbort, listener, build, dingDingSign.getSign());
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
