package pl.engine.mmorpg.entity;

import pl.engine.mmorpg.animation.AnimatedMesh;

public record AnimationInfo(
    String path,
    float animationSpeedMultiplier
) {

    public AnimationInfo(String path){

        this(path, AnimatedMesh.DEFAULT_NUMBER_OF_TICS_PER_SECOND);
    }
}
