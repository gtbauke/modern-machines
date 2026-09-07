package io.github.gtbauke.modernmachines.api.animation;

import org.jspecify.annotations.NonNull;

public interface IAnimatedMachine {
    @NonNull AnimationParameterContainer getAnimationParameters();

    void triggerAnimation(int triggerId);
}
