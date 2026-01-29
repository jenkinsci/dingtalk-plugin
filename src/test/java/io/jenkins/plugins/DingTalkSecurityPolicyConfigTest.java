package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jenkins.plugins.enums.SecurityPolicyEnum;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DingTalkSecurityPolicyConfigTest {

  @Test
  void ofCreatesConfigWithTypeDescAndEmptyValue() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.ENGLISH);
      DingTalkSecurityPolicyConfig config = DingTalkSecurityPolicyConfig.of(SecurityPolicyEnum.KEY);

      assertEquals(SecurityPolicyEnum.KEY.name(), config.getType());
      assertEquals(SecurityPolicyEnum.KEY.getDesc(), config.getDesc());
      assertEquals("", config.getValue());
    } finally {
      Locale.setDefault(originalLocale);
    }
  }
}
