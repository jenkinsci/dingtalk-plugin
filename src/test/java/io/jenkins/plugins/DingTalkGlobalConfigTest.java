package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jenkins.plugins.enums.NoticeOccasionEnum;
import io.jenkins.plugins.model.NoticeOccasionOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DingTalkGlobalConfigTest {

  @Test
  void buildNoticeOccasionRowsPreservesOrderAndColumns() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault(Locale.ENGLISH);

      List<List<NoticeOccasionOption>> rows = DingTalkGlobalConfig.buildNoticeOccasionRows();

      assertEquals(2, rows.size());
      assertEquals(3, rows.get(0).size());
      assertEquals(3, rows.get(1).size());

      List<String> names = rows.stream()
          .flatMap(List::stream)
          .map(NoticeOccasionOption::getName)
          .collect(Collectors.toList());
      List<String> expectedNames = Arrays.stream(NoticeOccasionEnum.values())
          .map(Enum::name)
          .collect(Collectors.toList());
      assertEquals(expectedNames, names);

      List<String> labels = rows.stream()
          .flatMap(List::stream)
          .map(NoticeOccasionOption::getLabel)
          .collect(Collectors.toList());
      List<String> expectedLabels = Arrays.stream(NoticeOccasionEnum.values())
          .map(NoticeOccasionEnum::getDesc)
          .collect(Collectors.toList());
      assertEquals(expectedLabels, labels);
    } finally {
      Locale.setDefault(originalLocale);
    }
  }
}
