package io.jenkins.plugins.tools;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import lombok.NoArgsConstructor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 用参数来代替换行拼接，方便书写 markdown 内容
 *
 * @author liuwei
 * @date 2020/3/29 13:04
 */
@Symbol("markdown")
@Extension
@NoArgsConstructor
public class Markdown extends Descriptor<Markdown> implements Describable<Markdown> {

  private String value;

  @DataBoundConstructor
  public Markdown(String... args) {
    if (args != null) {
      this.value = String.join("\n", args);
    }
  }

  public String value() {
    return value;
  }

  @Override
  public Descriptor<Markdown> getDescriptor() {
    Jenkins instance = Jenkins.getInstanceOrNull();
    if (instance == null) {
      return null;
    }
    return instance.getDescriptorByType(Markdown.class);
  }
}
