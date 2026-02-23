package pl.engine.mmorpg.entity.move;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Entity;

public class MoveComponent {

    private final Entity entity;

    private boolean isSprinting = true;

    private Vector3f wantMove = new Vector3f();

    private static final double RUN_SENS = 6;
    protected static final double MOVE_SENS = 2;
    protected static final double JUMP_SENS = 10;

    public MoveComponent(Entity entity){

        this.entity = entity;
    }

    public void resetMove(){

        wantMove.x = 0;
        wantMove.y = 0;//
        wantMove.z = 0;

        entity.setMoveDirectionState(MoveDirectionState.FRONT);
        entity.setMoveState(MoveState.STANDING);
    }
    
    public void switchIsSprinting(){
        
        this.isSprinting = !this.isSprinting;
    }

    public void moveForward(double deltaTimeInSeconds, Vector3f forward){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInForward(moveValue, forward);

        entity.setMoveDirectionState((MoveDirectionState.FRONT));
        updateMoveState();
    }

    public void moveBackward(double deltaTimeInSeconds, Vector3f forward){

        double moveValue = getMoveValue(deltaTimeInSeconds) * 0.5;
        moveInForward(-moveValue, forward);

        entity.setMoveDirectionState((MoveDirectionState.BACK));
        updateMoveState();
    }

    public void moveTop(double deltaTimeInSeconds){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        wantMove.y += moveValue * JUMP_SENS;

        entity.setMoveDirectionState((MoveDirectionState.TOP));
        entity.setMoveState(MoveState.JUMP);
    }

    public void moveDown(double deltaTimeInSeconds){

        double moveValue = getMoveValue(deltaTimeInSeconds);
        wantMove.y -= moveValue;

        entity.setMoveState(MoveState.STANDING);
    }

    public void moveLeft(double deltaTimeInSeconds, Vector3f forward){

        forward.rotateY((float) Math.toRadians(90));

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInDirection(moveValue, forward);

        entity.setMoveDirectionState(MoveDirectionState.LEFT);
        updateMoveState();
    }

    public void moveRight(double deltaTimeInSeconds, Vector3f forward){

        forward.rotateY((float) Math.toRadians(90));

        double moveValue = getMoveValue(deltaTimeInSeconds);
        moveInDirection(-moveValue, forward);

        entity.setMoveDirectionState(MoveDirectionState.RIGHT);
        updateMoveState();
    }

    private void moveInForward(double scale, Vector3f forward){

        moveInDirection(scale, forward);
    }

    private void moveInDirection(double scale, Vector3f dir){

        wantMove.x = (float) (scale * dir.x);
        wantMove.y += scale * dir.y;
        wantMove.z += scale * dir.z;
    }

    private void updateMoveState(){

        if(isSprinting){

            entity.setMoveState(MoveState.RUN);
        }
        else{

            entity.setMoveState(MoveState.WALK);
        }
    }

    public boolean wasMoved(){

        return !wantMove.equals(0, 0, 0);
    }

    public Vector3f getWantMove(){

        return wantMove;
    }

    private double getMoveValue(double deltaTimeInSeconds){

        double moveMultiplier = isSprinting ? RUN_SENS : MOVE_SENS;

        return deltaTimeInSeconds * moveMultiplier;
    }
}
