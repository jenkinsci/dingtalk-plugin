package io.jenkins.plugins.sdk;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author liuwei
 */
@Data
public class HttpResponse{

  private String body;

  private Map<String, List<String>> headers;
}
