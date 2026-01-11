package pl.engine.mmorpg.mesh.libraries.jgltf;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.animation.libraries.jgltf.AnimatedComplexJgltfMesh;
import pl.engine.mmorpg.animation.libraries.jgltf.JgltfGlbSkeleton;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.Mesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.texture.JgltfTexture;
import pl.engine.mmorpg.texture.Texture;

public class JgltfMeshAbstractFactory extends MeshAbstractFactory {

    @Override
    public Mesh createMesh(Object meshData, Texture texture) {

        return new JgltfGlbMesh((MeshModel) meshData, (JgltfTexture) texture);
    }

    @Override
    public ComplexMesh createComplexMesh(String complexModelFilePath) {

        return new ComplexJgltfMesh(complexModelFilePath);
    }

    @Override
    public AnimatedMeshable createComplexAnimatedMesh(String complexModelFilePath, Skeleton skeleton) {

        return new AnimatedComplexJgltfMesh(complexModelFilePath, skeleton);
    }

    @Override
    public AnimatedMeshable createComplexAnimatedMesh(String complexModelFilePath) {

        return new AnimatedComplexJgltfMesh(complexModelFilePath);
    }
}
