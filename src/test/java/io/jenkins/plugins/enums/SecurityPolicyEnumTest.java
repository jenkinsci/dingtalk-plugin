package io.jenkins.plugins.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class SecurityPolicyEnumTest {

  @Test
  void getDescRespectsLocale() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.ENGLISH);
      assertEquals("Custom Keywords", SecurityPolicyEnum.KEY.getDesc());

      Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
      assertEquals("自定义关键词", SecurityPolicyEnum.KEY.getDesc());
    } finally {
      Locale.setDefault(originalLocale);
    }
  }
}
