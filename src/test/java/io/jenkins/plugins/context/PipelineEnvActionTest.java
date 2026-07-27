package io.jenkins.plugins.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.EnvVars;
import hudson.util.XStream2;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class PipelineEnvActionTest {

  @Test
  void environmentIsNeverPersisted() {
    // The accumulated environment can include credentials bound by withCredentials, and Jenkins
    // saves the build record on its own, so none of it may end up in build.xml.
    PipelineEnvAction action = new PipelineEnvAction();
    action.merge(new EnvVars("MY_SECRET", "s3cr3t-must-not-be-written"));

    String xml = new XStream2().toXML(action);

    assertFalse(
        xml.contains("s3cr3t-must-not-be-written"),
        "credential written to build record: " + xml);
    assertFalse(xml.contains("MY_SECRET"), "variable name written to build record: " + xml);
  }

  @Test
  void environmentFieldStaysTransient() throws Exception {
    // The assertion above relies on this modifier, and dropping it breaks neither the build nor
    // any behaviour, so pin it separately.
    Field field = PipelineEnvAction.class.getDeclaredField("vars");
    assertTrue(
        Modifier.isTransient(field.getModifiers()),
        "vars must stay transient, otherwise credentials are written to build.xml in plain text");
  }

  @Test
  void mergeAccumulates() {
    PipelineEnvAction action = new PipelineEnvAction();
    action.merge(new EnvVars("A", "1"));
    action.merge(new EnvVars("B", "2"));

    EnvVars merged = action.snapshot();
    assertEquals("1", merged.get("A"));
    assertEquals("2", merged.get("B"));
  }

  @Test
  void laterMergeOverridesEarlierValue() {
    PipelineEnvAction action = new PipelineEnvAction();
    action.merge(new EnvVars("K", "old"));
    action.merge(new EnvVars("K", "new"));

    assertEquals("new", action.snapshot().get("K"));
  }

  @Test
  void snapshotIsIndependentOfLaterChanges() {
    // EnvVars extends TreeMap and is not thread safe, so a snapshot handed out must not be
    // rewritten by a later merge.
    PipelineEnvAction action = new PipelineEnvAction();
    action.merge(new EnvVars("K", "v1"));
    EnvVars snapshot = action.snapshot();

    action.merge(new EnvVars("K", "v2"));
    snapshot.put("K", "mutated-by-caller");

    assertEquals("v2", action.snapshot().get("K"));
  }

  @Test
  void snapshotOfEmptyActionIsEmpty() {
    assertTrue(new PipelineEnvAction().snapshot().isEmpty());
  }
}
