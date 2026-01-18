package pl.engine.mmorpg.mesh;

import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.texture.Texture;

public abstract class MeshAbstractFactory {

    public abstract Mesh createMesh(Object meshData, Texture texture);
    public abstract ComplexMesh createComplexMesh(String complexModelFilePath);
    public abstract AnimatedMeshable createComplexAnimatedMesh(ComplexMesh model, String animatedModelPath);
    public abstract AnimatedMeshable createComplexAnimatedMesh(ComplexMesh model, String animatedModelPath, Skeleton skeleton);
    public abstract Skeleton createSkeleton(Object data);
}
