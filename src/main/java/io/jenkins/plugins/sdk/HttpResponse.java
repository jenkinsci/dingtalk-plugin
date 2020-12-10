package io.jenkins.plugins.sdk;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author liuwei
 * @date 2020-12-09 21:35:58
 */
@Data
public class HttpResponse implements Serializable {

  private String body;

  private Map<String, List<String>> headers;
}
