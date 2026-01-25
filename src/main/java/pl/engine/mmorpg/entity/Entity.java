package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.*;

public class Entity implements Meshable {

    protected ComplexMesh mesh;
    protected Skeleton skeleton;
    protected double deltaTimeInSeconds = 0;

    protected MoveDirectionState moveDirectionState = MoveDirectionState.FRONT;
    protected MoveState moveState = MoveState.STANDING;
    protected CombatState combatState = CombatState.NO_WEAPON;

    private final CombinedAnimationController combinedAnimationController;

    protected static final double ROTATION_SENS = 50000;
    protected static final double MOVE_SENS = 2;

    public Entity(
        String modelPath,
        Map<String, String> animationsKeysPathsMappings,
        MeshAbstractFactory meshFactory,
        String firstAnimationName
    ){
        this.mesh = meshFactory.createComplexMesh(modelPath);
        this.skeleton = meshFactory.createSkeleton(mesh.getData());

        this.combinedAnimationController = new CombinedAnimationController(
            animationsKeysPathsMappings,
            meshFactory,
            firstAnimationName
        );
    }

    @Override
    public void uploadToGpu() {

        this.combinedAnimationController.init(this);
        mesh.uploadToGpu();

        this.combinedAnimationController.uploadToGpu();
    }

    @Override
    public void setModel(Matrix4f model) {

        mesh.setModel(model);
    }

    @Override
    public void draw() {

        combinedAnimationController.draw();
    }

    @Override
    public void clear() {

        mesh.clear();
        combinedAnimationController.clear();
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        combinedAnimationController.update(deltaTimeInSeconds, moveDirectionState, moveState, combatState);
        this.deltaTimeInSeconds = deltaTimeInSeconds;
    }

    @Override
    public int getNumberOfVertices() {
        return mesh.getNumberOfVertices();
    }

    @Override
    public int getNumberOfFaces() {

        return mesh.getNumberOfFaces();
    }

    @Override
    public float[] getVertices() {

        return mesh.getVertices();
    }

    @Override
    public int[] getFaces() {

        return mesh.getFaces();
    }

    public ComplexMesh getComplexMesh(){

        return mesh;
    }
}
