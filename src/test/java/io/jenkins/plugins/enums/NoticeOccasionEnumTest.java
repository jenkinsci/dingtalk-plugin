package io.jenkins.plugins.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class NoticeOccasionEnumTest {

  @Test
  void getDescRespectsLocale() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.ENGLISH);
      assertEquals("Build started", NoticeOccasionEnum.START.getDesc());

      Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
      assertEquals("构建启动时", NoticeOccasionEnum.START.getDesc());
    } finally {
      Locale.setDefault(originalLocale);
    }
  }
}
