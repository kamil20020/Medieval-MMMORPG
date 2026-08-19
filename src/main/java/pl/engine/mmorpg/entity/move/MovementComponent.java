package pl.engine.mmorpg.entity.move;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.input.InputData;

public class MovementComponent implements Component {

    private final InputData inputData;
    private final EntityStateData entityStateData;
    private final TransformComponent transformComponent;

    private boolean wasMoved = false;
    private MoveDirectionState moveDirectionState = MoveDirectionState.FRONT;

    private final Vector3f velocity = new Vector3f();

    private static final double RUN_SENS = 6;
    protected static final double MOVE_SENS = 2;
    protected static final double JUMP_SENS = 15;
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
            entityStateData.isSprinting = !entityStateData.isSprinting;
        }

        updateRotationMovement(deltaTimeInSeconds);

        if(!entityStateData.canActionBeInterrupted){
            return;
        }

        wasMoved = false;

        updateHorizontalMovement(deltaTimeInSeconds, forward);
        updateVerticalMovement(deltaTimeInSeconds);

        if(wasMoved){

            entityStateData.entityState = EntityState.MOVE;
        }
    }

    private void updateHorizontalMovement(double deltaTimeInSeconds, Vector3f forward){

        if(inputData.moveFront){
            moveDirectionState = MoveDirectionState.FRONT;
            moveForward(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
        else if(inputData.moveBack){
            moveDirectionState = MoveDirectionState.BACK;
            moveBackward(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
        else if(inputData.moveRight){
            moveDirectionState = MoveDirectionState.RIGHT;
            moveRight(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
        else if(inputData.moveLeft){
            moveDirectionState = MoveDirectionState.LEFT;
            moveLeft(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
    }

    private void updateVerticalMovement(double deltaTimeInSeconds){

        if(inputData.moveTop && !entityStateData.isInAir){
            moveDirectionState = MoveDirectionState.TOP;
            moveTop(deltaTimeInSeconds);
            wasMoved = true;
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

    private double getMoveValue(double deltaTimeInSeconds){

        return getMoveValue(deltaTimeInSeconds, 1d);
    }

    private double getMoveValue(double deltaTimeInSeconds, double moveMultiplier){

        double moveTypeMultiplier = entityStateData.isSprinting ? RUN_SENS : MOVE_SENS;

        return deltaTimeInSeconds * moveTypeMultiplier * moveMultiplier;
    }

    private void updateRotationMovement(double deltaTime){

        updateHorizontalRotationMovement(deltaTime);
        updateVerticalRotationMovement(deltaTime);
    }

    private void updateHorizontalRotationMovement(double deltaTime){

        double rotationValue = Math.abs(inputData.mouseXPosForWindowWidth) * ROTATION_SENS * deltaTime;

        if(inputData.rotateLeft){
            rotateLeft(rotationValue);
        }
        else if(inputData.rotateRight){
            rotateRight(rotationValue);
        }
    }

    private void updateVerticalRotationMovement(double deltaTime){

        double rotationValue = Math.abs(inputData.mouseYPosForWindowHeight) * ROTATION_SENS * deltaTime;

        if(inputData.rotateDown){
            rotateTop(rotationValue);
        }
        else if(inputData.rotateTop){
            rotateDown(rotationValue);
        }
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

    public Vector3f getVelocity(){

        return velocity;
    }

    public MoveDirectionState getMoveDirectionState(){

        return moveDirectionState;
    }

    @Override
    public void clear(){

        velocity.x = 0;
        velocity.z = 0;
    }

    @Override
    public void save(){

        transformComponent.move(velocity);
    }
}
