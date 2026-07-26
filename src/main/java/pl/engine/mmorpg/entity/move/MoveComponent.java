package pl.engine.mmorpg.entity.move;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Entity;
import pl.engine.mmorpg.entity.input.InputComponent;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class MoveComponent {

    private MoveState moveState = MoveState.STANDING;
    private MoveDirectionState moveDirectionState = MoveDirectionState.FRONT;

    private boolean isSprinting = true;

    private Vector3f velocity = new Vector3f();

    private static final double RUN_SENS = 6;
    protected static final double MOVE_SENS = 2;
    protected static final double JUMP_SENS = 20;

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

    public void resetHorizontal(){

        velocity.x = 0;
        velocity.z = 0;
    }

    public void resetState(){

        moveState = MoveState.STANDING;
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

    public MoveState getMoveState(){

        return moveState;
    }

    public void setMoveState(MoveState moveState){

        this.moveState = moveState;
    }

    public MoveDirectionState getMoveDirectionState(){

        return moveDirectionState;
    }

    public void setMoveDirectionState(MoveDirectionState moveDirectionState){

        this.moveDirectionState = moveDirectionState;
    }

    private void updateMoveSpeedState(){
        moveState = isSprinting ? MoveState.RUN : MoveState.WALK;
    }

    public void update(InputComponent inputComponent, double deltaTimeInSeconds, Vector3f forward){

        if(inputComponent.switchSprintPressed){
            isSprinting = !isSprinting;
        }

        moveState = MoveState.STANDING;

        if(inputComponent.moveFront){
            moveDirectionState = MoveDirectionState.FRONT;
            updateMoveSpeedState();
            moveForward(deltaTimeInSeconds, forward);
        }
        else if(inputComponent.moveBack){
            moveDirectionState = MoveDirectionState.BACK;
            updateMoveSpeedState();
            moveBackward(deltaTimeInSeconds, forward);
        }
        else if(inputComponent.moveRight){
            moveDirectionState = MoveDirectionState.RIGHT;
            updateMoveSpeedState();
            moveRight(deltaTimeInSeconds, forward);
        }
        else if(inputComponent.moveLeft){
            moveDirectionState = MoveDirectionState.LEFT;
            updateMoveSpeedState();
            moveLeft(deltaTimeInSeconds, forward);
        }

        handleFalling();

        if(inputComponent.moveTop){
            moveState = MoveState.JUMP;
            moveDirectionState = MoveDirectionState.TOP;
            moveTop(deltaTimeInSeconds);
        }
    }

    public boolean isActive(){

        return moveState != MoveState.STANDING;
    }
}
