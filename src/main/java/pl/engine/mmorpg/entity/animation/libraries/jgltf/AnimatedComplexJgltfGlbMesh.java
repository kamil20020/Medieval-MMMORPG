package pl.engine.mmorpg.entity.animation.libraries.jgltf;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import pl.engine.mmorpg.entity.animation.Skeleton;
import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.mesh.libraries.jgltf.ComplexJgltfGlbMesh;
import pl.engine.mmorpg.mesh.libraries.jgltf.JgltfGlbMesh;
import pl.engine.mmorpg.texture.JgltfTexture;

import java.util.List;

public class AnimatedComplexJgltfGlbMesh extends ComplexJgltfGlbMesh {

    private final Skeleton skeleton;
    private final GltfModel animatedModel;

    public AnimatedComplexJgltfGlbMesh(String animatedComplexModelFilePath) {

        this.animatedModel = ComplexJgltfGlbMesh.load(animatedComplexModelFilePath);
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

            if(AnimatedJgltfGlbMesh.isAnimated(rawMesh)){

                mesh = new AnimatedJgltfGlbMesh(animatedModel, rawMesh, texture, skeleton);
            }
            else{

                mesh = new JgltfGlbMesh(rawMesh, texture);
            }

            meshes.add(mesh);
        }
    }
}

