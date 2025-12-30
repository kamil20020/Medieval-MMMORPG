package org.example.mesh.libraries.assimp.animation;

import org.example.mesh.animation.Skeleton;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

public class AssimpGlbSkeleton extends Skeleton {

    private final AIScene scene;

    public AssimpGlbSkeleton(AIScene scene){

        this.scene = scene;

        loadBonesNamesIndices();
    }

    @Override
    protected void loadBonesNamesIndices() {

        PointerBuffer meshes = scene.mMeshes();

        for(int meshIndex = 0; meshIndex < scene.mNumMeshes(); meshIndex++){

            long meshId = meshes.get(meshIndex);
            AIMesh mesh = AIMesh.create(meshId);

            loadMeshBones(mesh);
        }
    }

    private void loadMeshBones(AIMesh mesh){

        PointerBuffer bones = mesh.mBones();

        for(int boneIndex = 0; boneIndex < mesh.mNumBones(); boneIndex++){

            long boneId = bones.get(boneIndex);
            AIBone bone = AIBone.create(boneId);
            String boneName = bone.mName().dataString();

            if(!containsBone(boneName)){

                addBone(boneName, boneIndex);
            }
        }
    }
}
