package pl.engine.mmorpg.animation.libraries.jgltf;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.DynamicMesh;
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
    private float numberOfTicksPerSecond;

    public AnimatedComplexJgltfMesh(ComplexJgltfMesh complexMesh, String animatedComplexModelFilePath, float numberOfTicksPerSecond) {

        this.model = complexMesh;
        this.animatedModel = ComplexJgltfMesh.load(animatedComplexModelFilePath);
        this.skeleton = new JgltfGlbSkeleton(animatedModel);
        this.numberOfTicksPerSecond = numberOfTicksPerSecond;

        loadModel(null);
    }

    public AnimatedComplexJgltfMesh(ComplexJgltfMesh complexMesh, String animatedComplexModelFilePath) {

        this(complexMesh, animatedComplexModelFilePath, AnimatedMesh.DEFAULT_NUMBER_OF_TICS_PER_SECOND);
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

                animatedMesh = new AnimatedJgltfMesh(mesh, rawMesh, animatedModel, skeleton, numberOfTicksPerSecond);
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

        List<Matrix4f[]> result = new ArrayList<>();

        for(Meshable mesh : meshes){

            AnimatedMesh animatedMesh = (AnimatedMesh) mesh;
            result.add(animatedMesh.getBoneFinalTransformations());
        }

        return result;
    }

    @Override
    public void addDynamicMesh(DynamicMesh dynamicMesh) {

        AnimatedMesh firstAnimatedMesh = (AnimatedMesh) meshes.get(0);
        firstAnimatedMesh.addDynamicMesh(dynamicMesh);
    }

    @Override
    public void setNextAnimation(AnimatedMeshable nextAnimation){

        for(int i = 0; i < meshes.size(); i++){

            AnimatedMesh actualAnimationMesh = getAnimatedMesh(i);
            AnimatedMesh nextAnimationMesh = nextAnimation.getAnimatedMesh(i);
            actualAnimationMesh.setNextAnimation(nextAnimationMesh);
        }
    }

    @Override
    public void setBlendingProgress(float blendingProgress) {

        for(int i = 0; i < meshes.size(); i++){

            AnimatedMesh animatedMesh = getAnimatedMesh(i);
            animatedMesh.setBlendingProgress(blendingProgress);
        }
    }
}

