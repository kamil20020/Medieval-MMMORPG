package pl.engine.mmorpg.animation.libraries.assimp;

import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.libraries.assimp.ComplexAssimpMesh;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import pl.engine.mmorpg.texture.AssimpTexture;

public class AnimatedComplexAssimpModel extends ComplexAssimpMesh {

    private final AIScene animatedScene;
    private final Skeleton skeleton;

    public AnimatedComplexAssimpModel(String animatedComplexModelFilePath) {

        this.animatedScene = loadScene(animatedComplexModelFilePath);
        this.skeleton = new AssimpGlbSkeleton(animatedScene);

        loadModel(animatedComplexModelFilePath);
    }

    @Override
    protected void loadModel(String complexModelFilePath){

        PointerBuffer meshesBuffer = animatedScene.mMeshes();

        for(int i = 0; i < animatedScene.mNumMeshes(); i ++){

            long meshId = meshesBuffer.get(i);
            AIMesh aiMesh = AIMesh.create(meshId);

            AssimpTexture texture = new AssimpTexture(animatedScene, aiMesh);
            Meshable mesh = new AnimatedAssimpMesh(aiMesh, animatedScene, texture, skeleton);

            meshes.add(mesh);
        }
    }
}
