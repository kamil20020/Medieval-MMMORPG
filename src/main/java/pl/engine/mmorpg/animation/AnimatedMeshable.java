package pl.engine.mmorpg.animation;

import org.joml.Matrix4f;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.List;

public interface AnimatedMeshable extends Meshable {

    public Skeleton getSkeleton();
    public void reset();
    public double getAnimationCompletion();
    public void addDynamicMesh(DynamicMesh dynamicMesh);
    public AnimatedMesh getAnimatedMesh(int index);
    public List<Matrix4f[]> getFinalBones();
}
