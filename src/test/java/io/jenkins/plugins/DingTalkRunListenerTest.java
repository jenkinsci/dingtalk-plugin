package io.jenkins.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.User;
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
}
