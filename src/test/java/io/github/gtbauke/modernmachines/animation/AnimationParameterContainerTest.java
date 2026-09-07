package io.github.gtbauke.modernmachines.animation;

import io.github.gtbauke.modernmachines.api.animation.AnimationParameterContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnimationParameterContainerTest {

    @Test
    public void testParameterDirtyTracking() {
        var container = new AnimationParameterContainer();

        Assertions.assertFalse(container.isDirty());

        container.setFloat("speed", 1.5f);
        Assertions.assertTrue(container.isDirty());
        Assertions.assertEquals(1.5f, container.getFloat("speed", 0.0f), 0.001f);

        container.clearDirty();
        Assertions.assertFalse(container.isDirty());

        // Setting same value shouldn't set dirty
        container.setFloat("speed", 1.5f);
        Assertions.assertFalse(container.isDirty());

        container.setBoolean("working", true);
        Assertions.assertTrue(container.isDirty());
        Assertions.assertTrue(container.getBoolean("working", false));
    }

    @Test
    public void testDefaultValues() {
        var container = new AnimationParameterContainer();

        Assertions.assertEquals(0.0f, container.getFloat("nonexistent", 0.0f), 0.001f);
        Assertions.assertFalse(container.getBoolean("nonexistent", false));
        Assertions.assertEquals(42, container.getInt("nonexistent", 42));
    }
}
