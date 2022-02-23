package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 */
@Getter
@Setter
@ToString
@Extension
public class DingTalkProxyConfig extends Descriptor<DingTalkProxyConfig>
    implements Describable<DingTalkProxyConfig> {

  private Type type;

  private String host;

  private Integer port;

  public void setHost(String host) {
    if (host != null) {
      this.host = host.trim();
    }
  }

  public DingTalkProxyConfig() {
    super(DingTalkProxyConfig.class);
  }

  @DataBoundConstructor
  public DingTalkProxyConfig(Type type, String host, int port) {
    this();
    this.type = type;
    this.host = host;
    this.port = port;
  }

  @Override
  public Descriptor<DingTalkProxyConfig> getDescriptor() {
    return this;
  }

  public Proxy getProxy() {

    /**
     * {@link Proxy#Proxy(Type, SocketAddress)} 防止抛异常
     */
    if (type == Type.DIRECT || StringUtils.isEmpty(host) || port == null) {
      return Proxy.NO_PROXY;
    }
    InetSocketAddress sa = new InetSocketAddress(host, port);
    return new Proxy(type, sa);
  }
}
