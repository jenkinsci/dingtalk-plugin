package io.jenkins.plugins.model;

public class NoticeOccasionOption {
  private final String name;
  private final String label;

  public NoticeOccasionOption(String name, String label) {
    this.name = name;
    this.label = label;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }
}
