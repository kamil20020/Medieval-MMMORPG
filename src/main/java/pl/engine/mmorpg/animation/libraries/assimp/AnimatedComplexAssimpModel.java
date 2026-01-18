package pl.engine.mmorpg.animation.libraries.assimp;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.libraries.assimp.ComplexAssimpMesh;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import pl.engine.mmorpg.texture.AssimpTexture;

import java.util.List;

public class AnimatedComplexAssimpModel extends ComplexAssimpMesh implements AnimatedMeshable {

    private final AIScene animatedScene;
    private final Skeleton skeleton;

    public AnimatedComplexAssimpModel(String animatedComplexModelFilePath, Skeleton skeleton) {

        this.animatedScene = loadScene(animatedComplexModelFilePath);
        this.skeleton = skeleton;

        loadModel(animatedComplexModelFilePath);
    }

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

    @Override
    public Skeleton getSkeleton() {

        return skeleton;
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
        return null;
    }

    @Override
    public List<Matrix4f[]> getFinalBones() {
        return null;
    }
}
