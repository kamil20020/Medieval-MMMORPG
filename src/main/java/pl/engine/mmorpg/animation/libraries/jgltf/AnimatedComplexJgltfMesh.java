package pl.engine.mmorpg.animation.libraries.jgltf;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.Mesh;
import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.mesh.libraries.jgltf.ComplexJgltfMesh;
import pl.engine.mmorpg.mesh.libraries.jgltf.JgltfGlbMesh;
import pl.engine.mmorpg.texture.JgltfTexture;

import java.util.ArrayList;
import java.util.List;

public class AnimatedComplexJgltfMesh extends ComplexJgltfMesh implements AnimatedMeshable {

    private final ComplexJgltfMesh model;
    private final Skeleton skeleton;
    private final GltfModel animatedModel;

    public AnimatedComplexJgltfMesh(ComplexJgltfMesh complexMesh, String animatedComplexModelFilePath, Skeleton skeleton) {

        this.model = complexMesh;
        this.animatedModel = ComplexJgltfMesh.load(animatedComplexModelFilePath);
        this.skeleton = skeleton;

        loadModel(null);
    }

    public AnimatedComplexJgltfMesh(ComplexJgltfMesh complexMesh, String animatedComplexModelFilePath) {

        this.model = complexMesh;
        this.animatedModel = ComplexJgltfMesh.load(animatedComplexModelFilePath);
        this.skeleton = new JgltfGlbSkeleton(animatedModel);

        loadModel(null);
    }

    @Override
    protected void loadModel(String complexModelFilePath) {

        GltfModel modelData = (GltfModel) model.getData();
        List<MeshModel> rawMeshes = modelData.getMeshModels();

        for (int i = 0; i < rawMeshes.size(); i++) {

            Mesh mesh = (Mesh) model.meshes.get(i);
            MeshModel rawMesh = rawMeshes.get(i);

            JgltfTexture texture = new JgltfTexture(rawMesh);

            Meshable animatedMesh = null;

            if(AnimatedJgltfMesh.isAnimated(rawMesh)){

                animatedMesh = new AnimatedJgltfMesh(mesh, rawMesh, animatedModel, skeleton);
            }
            else{

                animatedMesh = new JgltfGlbMesh(rawMesh, texture);
            }

            meshes.add(animatedMesh);
        }
    }

    public Skeleton getSkeleton(){

        return skeleton;
    }

    @Override
    public Object getData() {

        return model.getData();
    }

    @Override
    public void reset() {

        for(Meshable mesh : meshes){

            AnimatedMesh animatedMesh = (AnimatedMesh) mesh;
            animatedMesh.reset();
        }
    }

    @Override
    public double getAnimationCompletion() {

        AnimatedMesh animatedMesh = (AnimatedMesh) meshes.get(0);

        return animatedMesh.getAnimationCompletion();
    }

    @Override
    public AnimatedMesh getAnimatedMesh(int index) {
        return (AnimatedMesh) meshes.get(index);
    }

    @Override
    public List<Matrix4f[]> getFinalBones() {

        List<Matrix4f[]> aa = new ArrayList<>();

        for(Meshable mesh : meshes){

            AnimatedMesh animatedMesh = (AnimatedMesh) mesh;
            aa.add(animatedMesh.getBoneFinalTransformations());
        }
        return aa;
    }
}

