package pl.engine.mmorpg.entity.move;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Entity;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class MoveComponent {

    private MoveState moveState = MoveState.STANDING;
    private MoveDirectionState moveDirectionState = MoveDirectionState.FRONT;

    private boolean isSprinting = true;

    private Vector3f velocity = new Vector3f();

    private static final double RUN_SENS = 6;
    protected static final double MOVE_SENS = 2;
    protected static final double JUMP_SENS = 20;
    
    public void switchIsSprinting(){
        
        this.isSprinting = !this.isSprinting;
    }

    public void moveForward(double deltaTimeInSeconds, Vector3f forward){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInForward(moveValue, forward);

        moveDirectionState = MoveDirectionState.FRONT;
        updateMoveState();
    }

    public void moveBackward(double deltaTimeInSeconds, Vector3f forward){

        double moveValue = getMoveValue(deltaTimeInSeconds) * 0.5;
        moveInForward(-moveValue, forward);

        moveDirectionState = MoveDirectionState.BACK;
        updateMoveState();
    }

    public void handleVertical(){

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

        moveDirectionState = MoveDirectionState.TOP;
        moveState = MoveState.JUMP;
    }

    public void moveDown(double deltaTimeInSeconds){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        velocity.y -= moveValue;

        moveDirectionState = MoveDirectionState.DOWN;
        moveState = MoveState.JUMP;
    }

    public void moveLeft(double deltaTimeInSeconds, Vector3f forward){

        forward.rotateY((float) Math.toRadians(90));

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInDirection(moveValue, forward);

        moveDirectionState = MoveDirectionState.LEFT;
        updateMoveState();
    }

    public void moveRight(double deltaTimeInSeconds, Vector3f forward){

        forward.rotateY((float) Math.toRadians(90));

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInDirection(-moveValue, forward);

        moveDirectionState = MoveDirectionState.RIGHT;
        updateMoveState();
    }

    public void moveInForwardForDelta(double deltaTime, Vector3f forward){

        moveInDirection(getMoveValue(deltaTime), forward);
    }

    public void moveInForwardForDelta(double deltaTime, double moveMultiplier, Vector3f forward){

        moveInDirection(getMoveValue(deltaTime, moveMultiplier), forward);
    }

    private void moveInForward(double scale, Vector3f forward){

        moveInDirection(scale, forward);
    }

    private void moveInDirection(double scale, Vector3f dir){

        velocity.x += scale * dir.x;
        velocity.y += scale * dir.y;
        velocity.z += scale * dir.z;
    }

    private void updateMoveState(){

        if(isSprinting){

            moveState = MoveState.RUN;
        }
        else{

            moveState = MoveState.WALK;
        }
    }

    public void resetHorizontal(){

        velocity.x = 0;
        velocity.z = 0;
    }

    public void resetState(){

        moveState = MoveState.STANDING;
    }

    public boolean wasMoved(){

        return !velocity.equals(0, 0, 0);
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
}
