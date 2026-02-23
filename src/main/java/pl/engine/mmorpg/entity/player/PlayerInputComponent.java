package pl.engine.mmorpg.entity.player;

import org.joml.Vector3f;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.entity.GravityComponent;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.render.Camera;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerInputComponent {

    private final Camera camera;
    private final EventsHandler eventsHandler;
    private final Player player;
    private final MoveComponent playerMoveComponent;

    private boolean isVerticalCameraUnlocked = false;
    private boolean isInAir = false;
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
    }

    private void handleMove(double deltaTimeInSeconds){

        handleMoveWasd(deltaTimeInSeconds);
        handleMoveVertical(deltaTimeInSeconds);

//        setGravity();

        player.move(playerMoveComponent.getWantMove());
        camera.move(playerMoveComponent.getWantMove());
    }

    private void setGravity(){

        Vector3f newPlayerPosition = new Vector3f(player.getPosition());
        newPlayerPosition.add(playerMoveComponent.getWantMove());

        double newYMove = GravityComponent.getInstance().getNewYMove(newPlayerPosition);
        playerMoveComponent.getWantMove().y += (float) newYMove;

        if(Math.abs(playerMoveComponent.getWantMove().y) < GravityComponent.GRAVITY_SPEED_POSITIVE){
            playerMoveComponent.getWantMove().y = 0;
        }

        if(newYMove == 0){
            isInAir = false;
        }
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

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE)){//!isInAir
            playerMoveComponent.moveTop(deltaTimeInSeconds);
            isInAir = true;
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

            camera.rotateTop(rotationValue);
        }
        else{

            camera.rotateDown(rotationValue);
        }
    }
}
