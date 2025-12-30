package org.example.mesh.libraries.jgltf.animation;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import org.example.mesh.animation.Skeleton;

import java.util.List;

public class JgltfGlbSkeleton extends Skeleton {

    private final GltfModel model;

    public JgltfGlbSkeleton(GltfModel model){

        this.model = model;

        loadBonesNamesIndices();
    }

    @Override
    protected void loadBonesNamesIndices() {

        SkinModel firstSkinModel =  model.getSkinModels().get(0);

        List<NodeModel> bones = firstSkinModel.getJoints();

        for(int boneIndex = 0; boneIndex < bones.size(); boneIndex++){

            NodeModel bone = bones.get(boneIndex);
            String boneName = bone.getName();

            addBone(boneName, boneIndex);
        }
    }
}
