package io.github.gtbauke.modernmachines.api.client.animation.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.api.client.animation.math.Transform3D;
import io.github.gtbauke.modernmachines.api.client.animation.model.ModelPartNode;
import io.github.gtbauke.modernmachines.api.client.animation.timeline.KinematicDriver;
import io.github.gtbauke.modernmachines.api.client.animation.timeline.TimelineAction;
import org.jspecify.annotations.NonNull;

public class AnimationController {
    private final Map<String, List<KinematicDriver>> baseDrivers = new HashMap<>();
    private final Map<String, TimelineAction> timelines = new HashMap<>();
    private final Map<Integer, String> triggerMap = new HashMap<>();

    private final Map<String, Transform3D> timelineEvaluatedTransforms = new HashMap<>();

    private int tickCount = 0;

    public AnimationController() {
    }

    public AnimationController registerDriver(@NonNull String partName, @NonNull KinematicDriver driver) {
        baseDrivers.computeIfAbsent(partName, k -> new ArrayList<>()).add(driver);
        return this;
    }

    public AnimationController registerTimeline(@NonNull TimelineAction timeline) {
        timelines.put(timeline.getName(), timeline);
        return this;
    }

    public AnimationController timeline(@NonNull String name, @NonNull Consumer<TimelineAction> consumer) {
        var action = new TimelineAction(name);
        consumer.accept(action);
        timelines.put(name, action);
        return this;
    }

    public AnimationController registerTrigger(int triggerId, @NonNull String timelineName) {
        triggerMap.put(triggerId, timelineName);
        return this;
    }

    public void trigger(int triggerId) {
        var name = triggerMap.get(triggerId);
        if (name != null) {
            trigger(name);
        }
    }

    public void trigger(@NonNull String timelineName) {
        var action = timelines.get(timelineName);
        if (action != null) {
            action.play();
        }
    }

    public void tick(@NonNull ModelPartNode root) {
        tickCount++;

        // Reset base node transforms
        root.transform.reset();
        for (var child : root.getChildren()) {
            resetChildren(child);
        }

        // Apply Layer 0 (Kinematics)
        for (var entry : baseDrivers.entrySet()) {
            var node = root.findChild(entry.getKey());
            if (node != null) {
                for (var driver : entry.getValue()) {
                    driver.update(node.transform, tickCount);
                }
            }
        }

        // Tick timelines
        for (var action : timelines.values()) {
            action.tick();
        }

        // Update hierarchy previous transforms
        root.tick();
    }

    private void resetChildren(ModelPartNode node) {
        node.transform.reset();

        for (var child : node.getChildren()) {
            resetChildren(child);
        }
    }

    public void apply(float partialTick, @NonNull ModelPartNode root) {
        // Interpolate base node kinematics
        root.interpolate(partialTick);

        // Evaluate Layer 1 (Timelines)
        timelineEvaluatedTransforms.clear();
        for (var action : timelines.values()) {
            action.evaluate(partialTick, timelineEvaluatedTransforms);
        }

        // Blend timeline transforms on top of interpolated base render transforms
        for (var entry : timelineEvaluatedTransforms.entrySet()) {
            var node = root.findChild(entry.getKey());
            if (node != null) {
                node.renderTransform.add(entry.getValue());
            }
        }
    }
}
