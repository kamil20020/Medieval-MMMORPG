package org.example.mesh.libraries.jgltf.animation;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import org.example.mesh.Meshable;
import org.example.mesh.animation.Skeleton;
import org.example.mesh.libraries.jgltf.ComplexJgltfGlbMesh;
import texture.JgltfTexture;

import java.util.List;

public class AnimatedComplexJgltfGlbMesh extends ComplexJgltfGlbMesh {

    private final Skeleton skeleton;
    private final GltfModel model;
    private final GltfModel animatedModel;

    public AnimatedComplexJgltfGlbMesh(String complexModelFilePath, String animatedComplexModelFilePath) {

        this.model = load(complexModelFilePath);
        this.animatedModel = ComplexJgltfGlbMesh.load(animatedComplexModelFilePath);
        this.skeleton = new JgltfGlbSkeleton(animatedModel);

        loadModel(complexModelFilePath);
    }

    @Override
    protected void loadModel(String complexModelFilePath) {

        List<MeshModel> rawMeshes = model.getMeshModels();

        for (MeshModel rawMesh : rawMeshes) {

            JgltfTexture texture = new JgltfTexture(rawMesh);
            Meshable mesh = new AnimatedJgltfGlbMesh(animatedModel, rawMesh, texture, skeleton);

            meshes.add(mesh);
        }
    }
}

