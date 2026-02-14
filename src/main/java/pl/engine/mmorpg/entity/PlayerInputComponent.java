package pl.engine.mmorpg.entity;

import org.joml.Vector3f;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.render.Camera;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerInputComponent {

    private final Camera camera;
    private final EventsHandler eventsHandler;
    private final Entity player;
    private final MoveComponent playerMoveComponent;

    private boolean isVerticalCameraUnlocked = false;
    protected static final double ROTATION_SENS = 50000;

    public PlayerInputComponent(Player player, Camera camera, EventsHandler eventsHandler){

        this.player = player;
        this.playerMoveComponent = player.getPlayerMoveComponent();
        this.camera = camera;
        this.eventsHandler = eventsHandler;
    }

    public void update(double deltaTimeUInSeconds){

        handleKeyboard(deltaTimeUInSeconds);
        handleMouseRotate(deltaTimeUInSeconds);
    }

    private void handleKeyboard(double deltaTimeInSeconds){

        handleMove(deltaTimeInSeconds);

        if(eventsHandler.isKeyPressed(GLFW_KEY_R)){
            isVerticalCameraUnlocked = !isVerticalCameraUnlocked;
            eventsHandler.resetKey(GLFW_KEY_R);
        }
    }

    private void handleMove(double deltaTimeInSeconds){

        handleMoveWasd(deltaTimeInSeconds);

        if(eventsHandler.isKeyPressed(GLFW_KEY_UP)){
            camera.rotateTop(deltaTimeInSeconds);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_DOWN)){
            camera.rotateTop(deltaTimeInSeconds);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE)){
           playerMoveComponent.moveTop(deltaTimeInSeconds);
           camera.moveWithoutDirectionChange(playerMoveComponent.getWantMove().y);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_Z)){
            playerMoveComponent.moveDown(deltaTimeInSeconds);
            camera.moveWithoutDirectionChange(playerMoveComponent.getWantMove().y);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_V)){

            playerMoveComponent.switchIsSprinting();
            eventsHandler.resetKey(GLFW_KEY_V);
        }
    }

    private void handleMoveWasd(double deltaTimeInSeconds){

        Vector3f forward = camera.getForward();
        boolean wasMoved = false;

        if(eventsHandler.isKeyPressed(GLFW_KEY_W)){

            playerMoveComponent.moveForward(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_S)){

            playerMoveComponent.moveBackward(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_A)){

            playerMoveComponent.moveLeft(deltaTimeInSeconds, forward);
            wasMoved = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_D)){

            playerMoveComponent.moveRight(deltaTimeInSeconds, forward);
            wasMoved = true;
        }

        if(wasMoved){

            Vector3f wantMove = playerMoveComponent.getWantMove();
            camera.moveWithDirectionChange(wantMove);
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

            camera.rotateTop(rotationValue);
        }
        else{

            camera.rotateDown(rotationValue);
        }
    }
}
