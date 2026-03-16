package pl.engine.mmorpg.entity.player;

import org.joml.Vector3f;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.entity.GravityComponent;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.render.Camera;
import pl.engine.mmorpg.terrain.TerrainMesh;
import pl.engine.mmorpg.terrain.TerrainMeshHeightMapData;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerInputComponent {

    private final Camera camera;
    private final EventsHandler eventsHandler;
    private final Player player;
    private final MoveComponent playerMoveComponent;

    private boolean isVerticalCameraUnlocked = false;
    private boolean isInAir = false;
    private double timeStartInAir = 0;
    private boolean isTurnedOnGravity = true;
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

        if(eventsHandler.isKeyPressed(GLFW_KEY_P)){
            System.out.println("P " + (int)player.getPosition().x + " " + (int)player.getPosition().z);
            TerrainMesh terrainMesh = GravityComponent.getInstance().getTerrainMesh();
            Map<String, Float> heightMap = terrainMesh.getHeightMap();
            String key = TerrainMeshHeightMapData.getHeightMapKey(player.getPosition().x, player.getPosition().z);
            System.out.println("T " + heightMap.get(key).intValue() + "\n");
            eventsHandler.resetKey(GLFW_KEY_P);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_G)){
            isTurnedOnGravity = !isTurnedOnGravity;
            eventsHandler.resetKey(GLFW_KEY_G);
        }
    }

    private void handleMove(double deltaTimeInSeconds){

        handleMoveWasd(deltaTimeInSeconds);
        handleMoveVertical(deltaTimeInSeconds);

        if(isTurnedOnGravity){

            setGravity();
        }

        player.move(playerMoveComponent.getWantMove());
        camera.move(playerMoveComponent.getWantMove());
    }

    private void setGravity(){

        Vector3f newPlayerPosition = new Vector3f(player.getPosition());
        newPlayerPosition.add(playerMoveComponent.getWantMove());

        double seconds = 1;

        if(isInAir){

            seconds = glfwGetTime() - timeStartInAir;
        }

        double newYMove = GravityComponent.getInstance().getNewYMove(newPlayerPosition, seconds);
        playerMoveComponent.getWantMove().y += (float) newYMove;

        if(newYMove == 0){

            timeStartInAir = 0;
            isInAir = false;

            return;
        }

        if(!isInAir){

            timeStartInAir = glfwGetTime();
        }

        isInAir = true;

//        playerMoveComponent.handleVertical();
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

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE) && !isInAir){
            playerMoveComponent.moveTop(deltaTimeInSeconds);
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
