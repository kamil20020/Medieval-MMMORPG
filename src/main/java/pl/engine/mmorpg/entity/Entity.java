package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

public class Entity implements Meshable {

    protected AnimatedMeshable mesh;
    protected Skeleton skeleton;

    protected static final double ROTATION_SENS = 2;
    protected static final double MOVE_SENS = 0.1;

    public Entity(String modelPath, MeshAbstractFactory meshFactory){

//        mesh = new ComplexGlbMesh("models/warrior-sword.glb");
        this.mesh = meshFactory.createComplexAnimatedMesh(modelPath);
        this.skeleton = mesh.getSkeleton();
//        mesh = new AnimatedComplexJgltfGlbMesh(
//            "animations/dragon1.glb",
//            "animations/dragon1.glb"
//        );
//        mesh = new ComplexGlbMesh("animations/archer.glb");
//        mesh = new ComplexGlbMesh("animations/lecimy1.glb");
//        mesh = new ComplexGlbMesh("animations/test.fbx");
//        mesh = new ComplexGlbMesh("animations/fox.glb");
//        mesh = new ComplexGlbMesh("animations/human.glb");
//        mesh = new ComplexGlbMesh("animations/warrior1-fight.glb");
//        mesh = new AnimatedComplexGlbMesh("animations/test1.glb");
//        mesh = new AnimatedComplexGlbMesh("animations/warrior-standing-sword.glb");
//        mesh = new ComplexGlbMesh("animations/dragon.glb");
//        mesh = new ComplexGlbMesh("animations/testowe.glb");
//        mesh = new AnimatedComplexGlbMesh("animations/dragon1.glb");
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();
    }

    @Override
    public void setModel(Matrix4f model) {

        mesh.setModel(model);
    }

    @Override
    public void draw() {

        mesh.draw();
    }

    @Override
    public void clear() {

        mesh.clear();
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        mesh.update(deltaTimeInSeconds);
    }
}
