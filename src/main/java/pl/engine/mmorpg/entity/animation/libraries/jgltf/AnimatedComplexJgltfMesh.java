package pl.engine.mmorpg.entity.animation.libraries.jgltf;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import pl.engine.mmorpg.entity.animation.Skeleton;
import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.mesh.libraries.jgltf.ComplexJgltfMesh;
import pl.engine.mmorpg.mesh.libraries.jgltf.JgltfGlbMesh;
import pl.engine.mmorpg.texture.JgltfTexture;

import java.util.List;

public class AnimatedComplexJgltfMesh extends ComplexJgltfMesh {

    private final Skeleton skeleton;
    private final GltfModel animatedModel;

    public AnimatedComplexJgltfMesh(String animatedComplexModelFilePath) {

        this.animatedModel = ComplexJgltfMesh.load(animatedComplexModelFilePath);
        this.skeleton = new JgltfGlbSkeleton(animatedModel);

        loadModel(null);
    }

    @Override
    protected void loadModel(String complexModelFilePath) {

        List<MeshModel> rawMeshes = animatedModel.getMeshModels();

        for (int i = 0; i < rawMeshes.size(); i++) {

            MeshModel rawMesh = rawMeshes.get(i);

            JgltfTexture texture = new JgltfTexture(rawMesh);

            Meshable mesh = null;

            if(AnimatedJgltfMesh.isAnimated(rawMesh)){

                mesh = new AnimatedJgltfMesh(rawMesh, animatedModel, texture, skeleton);
            }
            else{

                mesh = new JgltfGlbMesh(rawMesh, texture);
            }

            meshes.add(mesh);
        }
    }
}

