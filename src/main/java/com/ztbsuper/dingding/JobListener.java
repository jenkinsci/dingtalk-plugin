package com.ztbsuper.dingding;

import com.ztbsuper.dingtalk.DingTalkNotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;

/**
 * Created by Marvin on 16/10/7.
 */
@Extension
public class JobListener extends RunListener<AbstractBuild> {


    public JobListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onStarted(AbstractBuild r, TaskListener listener) {
        for (DingdingService service : getService(r, listener)) {
            service.start();
        }
    }

    @Override
    public void onCompleted(AbstractBuild r, @Nonnull TaskListener listener) {
        Result result = r.getResult();
        if (null != result && result.equals(Result.SUCCESS)) {
            for (DingdingService service : getService(r, listener)) {
                service.success();
            }
        } else if (null != result && result.equals(Result.FAILURE)) {
            for (DingdingService service : getService(r, listener)) {
                service.failed();
            }
        } else {
            for (DingdingService service : getService(r, listener)) {
                service.abort();
            }
        }
    }

    private List<DingdingService> getService(AbstractBuild build, TaskListener listener) {
        Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
        List<DingdingService> services = new ArrayList<>();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof DingdingNotifier) {
                services.add(((DingdingNotifier) publisher).newDingdingService(build, listener));
            } else if (publisher instanceof DingTalkNotifier) {
                services.add(((DingTalkNotifier) publisher).newDingTalkService(build, listener));
            }
        }
        return services;
    }
}
