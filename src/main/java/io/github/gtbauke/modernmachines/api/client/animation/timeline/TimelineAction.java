package io.github.gtbauke.modernmachines.api.client.animation.timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.api.client.animation.math.Transform3D;
import org.jspecify.annotations.NonNull;

public class TimelineAction {
    private final String name;
    private final Map<String, AnimationTrack> tracks = new HashMap<>();
    private final Map<Integer, List<Runnable>> timelineHooks = new HashMap<>();

    private int currentTick = 0;
    private boolean playing = false;
    private boolean looping = false;
    private int totalDuration = 0;

    public TimelineAction(@NonNull String name) {
        this.name = name;
    }

    public @NonNull String getName() {
        return name;
    }

    public TimelineAction track(@NonNull String partName, @NonNull Consumer<AnimationTrack> trackConsumer) {
        var track = tracks.computeIfAbsent(partName, AnimationTrack::new);
        trackConsumer.accept(track);
        this.totalDuration = Math.max(this.totalDuration, track.getTotalDuration());
        return this;
    }

    public TimelineAction onTick(int tick, @NonNull Runnable callback) {
        timelineHooks.computeIfAbsent(tick, t -> new ArrayList<>()).add(callback);
        return this;
    }

    public TimelineAction setLooping(boolean looping) {
        this.looping = looping;
        return this;
    }

    public void play() {
        this.currentTick = 0;
        this.playing = true;
    }

    public void stop() {
        this.playing = false;
        this.currentTick = 0;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void tick() {
        if (!playing) {
            return;
        }

        var callbacks = timelineHooks.get(currentTick);
        if (callbacks != null) {
            for (var callback : callbacks) {
                callback.run();
            }
        }

        currentTick++;

        if (currentTick > totalDuration) {
            if (looping) {
                currentTick = 0;
            } else {
                playing = false;
            }
        }
    }

    public void evaluate(float partialTick, @NonNull Map<String, Transform3D> outTransforms) {
        if (!playing) {
            return;
        }

        for (var entry : tracks.entrySet()) {
            var partTransform = outTransforms.computeIfAbsent(entry.getKey(), k -> new Transform3D());
            entry.getValue().evaluate(currentTick, partialTick, partTransform);
        }
    }
}
