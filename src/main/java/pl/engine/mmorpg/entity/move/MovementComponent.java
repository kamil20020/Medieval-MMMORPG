package pl.engine.mmorpg.entity.move;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.input.InputData;

import java.util.function.Supplier;

public class MovementComponent implements Component {

    private InputData inputData;
    private EntityStateData entityStateData;
    private TransformComponent transformComponent;

    private MoveState moveState = MoveState.STANDING;
    private MoveDirectionState moveDirectionState = MoveDirectionState.FRONT;

    private boolean isSprinting = true;

    private Vector3f velocity = new Vector3f();

    private static final double RUN_SENS = 6;
    protected static final double MOVE_SENS = 2;
    protected static final double JUMP_SENS = 20;
    protected static final double ROTATION_SENS = 50000;

    public MovementComponent(InputData inputData, EntityStateData entityStateData, TransformComponent transformComponent){

        this.inputData = inputData;
        this.entityStateData = entityStateData;
        this.transformComponent = transformComponent;
    }

    @Override
    public void update(double deltaTimeInSeconds){

        Vector3f forward = transformComponent.getForward();

        if(inputData.switchSprintPressed){
            isSprinting = !isSprinting;
        }

        moveState = MoveState.STANDING;

        updateHorizontalMovement(deltaTimeInSeconds, forward);
        updateVerticalMovement(deltaTimeInSeconds);
        updateRotationMovement(deltaTimeInSeconds);

        if(moveState == MoveState.STANDING){
            velocity.x = 0;
            velocity.y = 0;
        }

        transformComponent.move(velocity);
    }

    private void updateHorizontalMovement(double deltaTimeInSeconds, Vector3f forward){

        if(inputData.moveFront){
            moveDirectionState = MoveDirectionState.FRONT;
            updateMoveSpeedState();
            moveForward(deltaTimeInSeconds, forward);
        }
        else if(inputData.moveBack){
            moveDirectionState = MoveDirectionState.BACK;
            updateMoveSpeedState();
            moveBackward(deltaTimeInSeconds, forward);
        }
        else if(inputData.moveRight){
            moveDirectionState = MoveDirectionState.RIGHT;
            updateMoveSpeedState();
            moveRight(deltaTimeInSeconds, forward);
        }
        else if(inputData.moveLeft){
            moveDirectionState = MoveDirectionState.LEFT;
            updateMoveSpeedState();
            moveLeft(deltaTimeInSeconds, forward);
        }
    }

    private void updateVerticalMovement(double deltaTimeInSeconds){

        handleFalling();

        if(inputData.moveTop && !entityStateData.isInAir){
            moveState = MoveState.JUMP;
            moveDirectionState = MoveDirectionState.TOP;
            moveTop(deltaTimeInSeconds);
        }
    }

    private void updateRotationMovement(double deltaTime){

        if(inputData.rotateLeft){
            rotateLeft(-deltaTime);
        }
        else if(inputData.rotateRight){
            rotateRight(deltaTime);
        }
        else if(inputData.rotateTop){
            rotateTop(-deltaTime);
        }
        else if(inputData.rotateDown){
            rotateDown(deltaTime);
        }
    }

    public void moveForward(double deltaTimeInSeconds, Vector3f forward){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInForward(moveValue, forward);
    }

    public void moveBackward(double deltaTimeInSeconds, Vector3f forward){

        double moveValue = getMoveValue(deltaTimeInSeconds) * 0.5;
        moveInForward(-moveValue, forward);
    }

    public void handleFalling(){

        if(velocity.y == 0 || Math.abs(velocity.y) < 0.5){
            return;
        }

        moveState = MoveState.JUMP;

        if(velocity.y < 0){

            moveDirectionState = MoveDirectionState.DOWN;
        }
        else{

            moveDirectionState = MoveDirectionState.TOP;
        }
    }

    public void moveTop(double deltaTimeInSeconds){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        velocity.y += moveValue * JUMP_SENS;
    }

    public void moveLeft(double deltaTimeInSeconds, Vector3f forward){

        forward.rotateY((float) Math.toRadians(90));

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInDirection(moveValue, forward);
    }

    public void moveRight(double deltaTimeInSeconds, Vector3f forward){

        forward.rotateY((float) Math.toRadians(90));

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInDirection(-moveValue, forward);
    }

    private void moveInForward(double scale, Vector3f forward){

        moveInDirection(scale, forward);
    }

    private void moveInDirection(double scale, Vector3f dir){

        velocity.x += scale * dir.x;
        velocity.y += scale * dir.y;
        velocity.z += scale * dir.z;
    }

    public Vector3f getVelocity(){

        return velocity;
    }

    private double getMoveValue(double deltaTimeInSeconds){

        return getMoveValue(deltaTimeInSeconds, 1d);
    }

    private double getMoveValue(double deltaTimeInSeconds, double moveMultiplier){

        double moveTypeMultiplier = isSprinting ? RUN_SENS : MOVE_SENS;

        return deltaTimeInSeconds * moveTypeMultiplier * moveMultiplier;
    }

    public void rotateLeft(double angle){

        Vector3f angles = transformComponent.getAngle();
        angles.y -= angle;
        angles.y %= 360;
        transformComponent.setAngle(angles);
    }

    public void rotateRight(double angle){

        Vector3f angles = transformComponent.getAngle();
        angles.y += angle;
        angles.y %= 360;
        transformComponent.setAngle(angles);
    }

    public void rotateTop(double angle){

        Vector3f angles = transformComponent.getAngle();
        angles.x += angle;
        angles.x = Math.max(-89, Math.min(89, angles.x));
        transformComponent.setAngle(angles);
    }

    public void rotateDown(double angle){

        Vector3f angles = transformComponent.getAngle();
        angles.x -= angle;
        angles.x = Math.max(-89, Math.min(89, angles.x));
        transformComponent.setAngle(angles);
    }

    public MoveState getMoveState(){

        return moveState;
    }

    public MoveDirectionState getMoveDirectionState(){

        return moveDirectionState;
    }

    private void updateMoveSpeedState(){

        moveState = isSprinting ? MoveState.RUN : MoveState.WALK;
    }
}
