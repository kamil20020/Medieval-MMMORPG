package pl.engine.mmorpg.entity.player;

import org.joml.Vector3f;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.entity.InputComponent;
import pl.engine.mmorpg.entity.TerrainCollisionComponent;
import pl.engine.mmorpg.entity.gravity.GravityComponent;
import pl.engine.mmorpg.entity.gravity.GravityMovementComponent;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.entity.move.MoveState;
import pl.engine.mmorpg.render.Camera;
import pl.engine.mmorpg.terrain.TerrainMesh;
import pl.engine.mmorpg.terrain.TerrainMeshHeightMapData;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerInputComponent implements InputComponent {

    private final Camera camera;
    private final EventsHandler eventsHandler;

    private final MoveComponent playerMoveComponent;

    private boolean isVerticalCameraUnlocked = false;
    private boolean isTurnedOnGravity = true;
    protected static final double ROTATION_SENS = 50000;

    public PlayerInputComponent(MoveComponent playerMoveComponent, Camera camera, EventsHandler eventsHandler){

        this.playerMoveComponent = playerMoveComponent;
        this.camera = camera;
        this.eventsHandler = eventsHandler;
    }

    @Override
    public void update(double deltaTimeUInSeconds){

        handleKeyboard(deltaTimeUInSeconds);
        handleMouseRotate(deltaTimeUInSeconds);
    }

    private void handleKeyboard(double deltaTimeInSeconds){

        handleMove(deltaTimeInSeconds);
        handleActions();
    }

    private void handleActions(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_V)){

            playerMoveComponent.switchIsSprinting();
            eventsHandler.resetKey(GLFW_KEY_V);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_R)){

            isVerticalCameraUnlocked = !isVerticalCameraUnlocked;
            eventsHandler.resetKey(GLFW_KEY_R);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_G)){
            isTurnedOnGravity = !isTurnedOnGravity;
            eventsHandler.resetKey(GLFW_KEY_G);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_M)){
            System.out.println(playerMoveComponent.getMoveState() + " " + playerMoveComponent.getMoveDirectionState());
            eventsHandler.resetKey(GLFW_KEY_M);
        }
    }

    private void handleMove(double deltaTimeInSeconds){

        if(playerMoveComponent.getMoveState() != MoveState.JUMP){

            handleMoveWasd(deltaTimeInSeconds);
        }

        handleMoveVertical(deltaTimeInSeconds);
    }

    private void handleMoveWasd(double deltaTimeInSeconds){

        Vector3f forward = camera.getForward();

        if(eventsHandler.isKeyPressed(GLFW_KEY_W)){

            playerMoveComponent.moveForward(deltaTimeInSeconds, forward);
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_S)){

            playerMoveComponent.moveBackward(deltaTimeInSeconds, forward);
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_A)){    

            playerMoveComponent.moveLeft(deltaTimeInSeconds, forward);
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_D)){

            playerMoveComponent.moveRight(deltaTimeInSeconds, forward);
        }
    }

    private void handleMoveVertical(double deltaTimeInSeconds){

        if(eventsHandler.isKeyPressed(GLFW_KEY_UP)){
            camera.rotateTop(deltaTimeInSeconds);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_DOWN)){
            camera.rotateTop(deltaTimeInSeconds);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE) && playerMoveComponent.getMoveState() != MoveState.JUMP){
            playerMoveComponent.moveTop(deltaTimeInSeconds);
            eventsHandler.resetKey(GLFW_KEY_SPACE);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_Z)){
            playerMoveComponent.moveDown(deltaTimeInSeconds);
        }
    }

    private void handleMouseRotate(double deltaTimeInSeconds){

        handleHorizontalRotate(deltaTimeInSeconds);

        if(isVerticalCameraUnlocked){

            handleVerticalRotate(deltaTimeInSeconds);
        }

        eventsHandler.resetMouseMove();
    }

    private void handleHorizontalRotate(double deltaTimeInSeconds){

        double mouseXPosForWindowWidth = eventsHandler.getMouseXPosForWindowWidth();

        if(mouseXPosForWindowWidth == 0){
            return;
        }

        double rotationValue = Math.abs(mouseXPosForWindowWidth) * ROTATION_SENS * deltaTimeInSeconds;

        if(mouseXPosForWindowWidth > 0){

            camera.rotateRight(rotationValue);
        }
        else{

            camera.rotateLeft(rotationValue);
        }
    }

    private void handleVerticalRotate(double deltaTimeInSeconds){

        double mouseYPosForWindowHeight = eventsHandler.getMouseYPosForWindowHeight();

        if(mouseYPosForWindowHeight == 0){
            return;
        }

        double rotationValue = Math.abs(mouseYPosForWindowHeight) * ROTATION_SENS * deltaTimeInSeconds;

        if(mouseYPosForWindowHeight > 0){

            camera.rotateDown(rotationValue);
        }
        else{

            camera.rotateTop(rotationValue);
        }
    }
}
