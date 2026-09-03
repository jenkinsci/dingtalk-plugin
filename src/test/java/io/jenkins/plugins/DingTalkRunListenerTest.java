package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.User;
import hudson.scm.ChangeLogSet;
import org.junit.jupiter.api.Test;

class DingTalkRunListenerTest {

  @Test
  void mobileIsNullWhenTheUserHasNoDingTalkProperty() {
    // User#getProperty is documented to return null, and a user that was loaded before this
    // plugin registered its property carries no DingTalkUserProperty at all.
    User user = mock(User.class);
    when(user.getProperty(DingTalkUserProperty.class)).thenReturn(null);

    assertNull(DingTalkRunListener.mobileOf(user));
  }

  @Test
  void mobileComesFromTheProperty() {
    User user = mock(User.class);
    when(user.getProperty(DingTalkUserProperty.class))
        .thenReturn(new DingTalkUserProperty("13800000000"));

    assertEquals("13800000000", DingTalkRunListener.mobileOf(user));
  }

  @Test
  void mobileIsNullWhenTheUserConfiguredNone() {
    User user = mock(User.class);
    when(user.getProperty(DingTalkUserProperty.class)).thenReturn(new DingTalkUserProperty(null));

    assertNull(DingTalkRunListener.mobileOf(user));
  }

  private static ChangeLogSet.Entry change(String commitId) {
    User author = mock(User.class);
    when(author.getDisplayName()).thenReturn("Bob Du");
    ChangeLogSet.Entry change = mock(ChangeLogSet.Entry.class);
    when(change.getCommitId()).thenReturn(commitId);
    when(change.getMsg()).thenReturn("fix: show the textarea");
    when(change.getAuthor()).thenReturn(author);
    return change;
  }

  @Test
  void summaryShortensTheCommitId() {
    assertEquals(
        "9d7b1cb fix: show the textarea（Bob Du）",
        DingTalkRunListener.summaryOf(change("9d7b1cb095c785248b8527f9465e7673c05d67ef")));
  }

  @Test
  void summaryOmitsTheIdWhenTheScmHasNone() {
    assertEquals("fix: show the textarea（Bob Du）", DingTalkRunListener.summaryOf(change(null)));
  }

  @Test
  void titleIsTheFirstLineOfTheMessage() {
    ChangeLogSet.Entry change = change(null);
    when(change.getMsg()).thenReturn("fix: show the textarea\r\n\r\nThe form offered the wrong one.");

    assertEquals("fix: show the textarea", DingTalkRunListener.titleOf(change));
    assertEquals("fix: show the textarea（Bob Du）", DingTalkRunListener.summaryOf(change));
  }
}
