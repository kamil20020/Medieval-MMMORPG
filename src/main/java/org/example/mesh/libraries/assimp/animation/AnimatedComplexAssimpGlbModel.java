package org.example.mesh.libraries.assimp.animation;

import org.example.mesh.Meshable;
import org.example.mesh.animation.Skeleton;
import org.example.mesh.libraries.assimp.ComplexAssimpGlbMesh;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import texture.AssimpTexture;

public class AnimatedComplexAssimpGlbModel extends ComplexAssimpGlbMesh {

    private final AIScene modelScene;
    private final AIScene animatedScene;
    private final Skeleton skeleton;

    public AnimatedComplexAssimpGlbModel(String complexModelFilePath, String animatedComplexModelFilePath) {

        this.modelScene = loadScene(complexModelFilePath);
        this.animatedScene = loadScene(animatedComplexModelFilePath);
        this.skeleton = new AssimpGlbSkeleton(modelScene);

        loadModel(complexModelFilePath);
    }

    @Override
    protected void loadModel(String complexModelFilePath){

        PointerBuffer meshesBuffer = modelScene.mMeshes();

        for(int i = 0; i < modelScene.mNumMeshes(); i ++){

            long meshId = meshesBuffer.get(i);
            AIMesh aiMesh = AIMesh.create(meshId);

            AssimpTexture texture = new AssimpTexture(modelScene, aiMesh);
            Meshable mesh = new AnimatedAssimpGlbMesh(animatedScene, aiMesh, texture, skeleton);

            meshes.add(mesh);
        }
    }
}
