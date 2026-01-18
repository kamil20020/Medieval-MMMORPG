package pl.engine.mmorpg.animation;

import pl.engine.mmorpg.mesh.Meshable;

public interface AnimatedMeshable extends Meshable {

    public Skeleton getSkeleton();
    public void reset();
    public double getAnimationCompletion();
}
