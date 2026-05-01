package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.entity.combat.CombatComponent;
import pl.engine.mmorpg.entity.combat.CombatState;
import pl.engine.mmorpg.entity.gravity.GravityComponent;
import pl.engine.mmorpg.entity.gravity.GravityMovementComponent;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.entity.move.MoveDirectionState;
import pl.engine.mmorpg.entity.move.MoveState;
import pl.engine.mmorpg.entity.player.Player;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.*;

public class Entity implements Meshable {

    protected ComplexMesh mesh;
    protected Skeleton skeleton;

    protected Vector3f position = new Vector3f(0, 0,0);

    protected MoveComponent moveComponent;
    protected CombatComponent combatComponent;

    protected GravityMovementComponent gravityMovementComponent;
    protected InputComponent inputComponent;

    private boolean isTurnedOnGravity = true;

    protected double deltaTimeInSeconds = 0;

    private final CombinedAnimationController combinedAnimationController;

    public Entity(
        String modelPath,
        Map<String, String> animationsKeysPathsMappings,
        MeshAbstractFactory meshFactory,
        String firstAnimationName
    ){
        this.mesh = meshFactory.createComplexMesh(modelPath);
        this.skeleton = meshFactory.createSkeleton(mesh.getData());

        this.moveComponent = new MoveComponent();
        this.combatComponent = new CombatComponent();
        this.gravityMovementComponent = new GravityMovementComponent(moveComponent);
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

        moveComponent.resetHorizontal();

        boolean isInAir = TerrainCollisionComponent.getInstance().isInAir(position);

        if(!isInAir){

            moveComponent.resetState();
        }

        inputComponent.update(deltaTimeInSeconds);

        if(isTurnedOnGravity){

            moveComponent.getVelocity().y -= GravityComponent.GRAVITY_SPEED;

            position = position.add(moveComponent.getVelocity());

            double collisionMove = TerrainCollisionComponent.getInstance().getCollisionMove(position);
            moveComponent.getVelocity().y += collisionMove;
        }
        else{
            position = position.add(moveComponent.getVelocity());
        }

        moveComponent.handleVertical();

        combinedAnimationController.update(deltaTimeInSeconds, moveComponent, combatComponent);

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

    public double getDeltaTimeInSeconds(){

        return deltaTimeInSeconds;
    }

    public Vector3f getPosition(){

        return position;
    }

    public MoveComponent getMoveComponent(){

        return moveComponent;
    }

    public GravityMovementComponent getGravityMovementComponent(){

        return gravityMovementComponent;
    }

    protected void setInputComponent(InputComponent inputComponent){

        this.inputComponent = inputComponent;
    }
}
