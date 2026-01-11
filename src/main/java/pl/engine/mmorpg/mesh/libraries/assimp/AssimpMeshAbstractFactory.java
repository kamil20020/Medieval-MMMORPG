package pl.engine.mmorpg.mesh.libraries.assimp;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.animation.libraries.assimp.AnimatedComplexAssimpModel;
import pl.engine.mmorpg.animation.libraries.assimp.AssimpGlbSkeleton;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.Mesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.texture.AssimpTexture;
import pl.engine.mmorpg.texture.Texture;

public class AssimpMeshAbstractFactory extends MeshAbstractFactory {

    @Override
    public Mesh createMesh(Object meshData, Texture texture) {

        return new AssimpGlbMesh((AIMesh) meshData, (AssimpTexture) texture);
    }

    @Override
    public ComplexMesh createComplexMesh(String complexModelFilePath) {

        return new ComplexAssimpMesh(complexModelFilePath);
    }

    @Override
    public AnimatedMeshable createComplexAnimatedMesh(String complexModelFilePath) {

        return new AnimatedComplexAssimpModel(complexModelFilePath);
    }

    @Override
    public AnimatedMeshable createComplexAnimatedMesh(String complexModelFilePath, Skeleton skeleton) {

        return new AnimatedComplexAssimpModel(complexModelFilePath, skeleton);
    }
}
