package com.ztbsuper;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.io.Serializable;
import java.util.Objects;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @Author: margo
 * @Date: 2019/11/7 17:12
 * @Description:
 */
@ExportedBean
public class KeyWord extends AbstractDescribableImpl<KeyWord> implements Serializable {

    private static final long serialVersionUID = -5892453082475036766L;

    private String keyword;

    @DataBoundConstructor
    public KeyWord(String keyword) {
        this.keyword = keyword;
    }

    @Exported
    public String getKeyword() {
        return keyword;
    }

    @DataBoundSetter
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public Descriptor<KeyWord> getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyWord)) {
            return false;
        }
        KeyWord keyWord = (KeyWord) o;
        return getKeyword().equals(keyWord.getKeyword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyword());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<KeyWord> {
        @Override
        public String getDisplayName() {
            return "关键词";
        }
    }
}
