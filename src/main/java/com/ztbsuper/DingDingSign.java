package com.ztbsuper;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @Author: margo
 * @Date: 2019/11/7 15:06
 * @Description:
 */
public class DingDingSign extends Sign implements Describable<DingDingSign> {

    private static final long serialVersionUID = 7882052906478003975L;

    @DataBoundConstructor
    public DingDingSign(String sign) {
        super(sign);
    }

    @Override
    public Descriptor<DingDingSign> getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(DingDingSignDescriptor.class);
    }

    @Extension
    public static class DingDingSignDescriptor extends Descriptor<DingDingSign> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.sign();
        }

    }
}
