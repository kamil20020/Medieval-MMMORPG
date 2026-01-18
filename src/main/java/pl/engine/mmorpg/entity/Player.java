package pl.engine.mmorpg.entity;

import pl.engine.mmorpg.render.Camera;
import pl.engine.mmorpg.EventsHandler;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static pl.engine.mmorpg.entity.CombinedAnimationController.*;

public class Player extends Entity {

    private Vector3f position;

    private final Camera camera;
    private final EventsHandler eventsHandler;
    private boolean isSprinting = true;
    private boolean isMoving = false;

    private static final String MODEL_PATH = "models/warrior.glb";
    private static final String FIRST_ANIMATION_NAME = getKey(MoveState.STANDING);

    private static final double RUN_SENS = 6;

    private static final Vector3f CAMERA_OFFSET = new Vector3f(0, -2, 2.5f);


    public Player(Camera camera, EventsHandler eventsHandler, MeshAbstractFactory meshFactory){
        super(MODEL_PATH, getAnimationNamesPathsMappings(), meshFactory, FIRST_ANIMATION_NAME);

        this.camera = camera;
        this.eventsHandler = eventsHandler;

        updatePositionForCamera();
    }

    private static Map<String, String> getAnimationNamesPathsMappings() {

        Map<String, String> result = new HashMap<>();

        result.put(getKey(MoveState.STANDING), "animations/warrior/idle.glb");
        result.put(getKey(MoveDirectionState.JUMP), "animations/warrior/move/jump.glb");

        result.put(getKey(MoveState.WALK, MoveDirectionState.FRONT), "animations/warrior/move/walk/front.glb");
        result.put(getKey(MoveState.WALK, MoveDirectionState.LEFT), "animations/warrior/move/walk/left.glb");
        result.put(getKey(MoveState.WALK, MoveDirectionState.RIGHT), "animations/warrior/move/walk/right.glb");
        result.put(getKey(MoveState.WALK, MoveDirectionState.BACK), "animations/warrior/move/walk/backward.glb");

        result.put(getKey(MoveState.RUN, MoveDirectionState.FRONT), "animations/warrior/move/run/front.glb");
        result.put(getKey(MoveState.RUN, MoveDirectionState.LEFT), "animations/warrior/move/run/left.glb");
        result.put(getKey(MoveState.RUN, MoveDirectionState.RIGHT), "animations/warrior/move/run/right.glb");
        result.put(getKey(MoveState.RUN, MoveDirectionState.BACK), "animations/warrior/move/run/backward.glb");

        result.put(getKey(CombatState.FIGHTING), "animations/warrior/combat/sword-inplace.glb");

        return result;
    }

    private void handleMove(){

        handleMoveWasd();

        double moveValue = deltaTimeInSeconds * MOVE_SENS;
        double rotationValue = deltaTimeInSeconds * ROTATION_SENS;

        if(eventsHandler.isKeyPressed(GLFW_KEY_UP)){
            camera.rotateTop(rotationValue);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_DOWN)){
            camera.rotateDown(rotationValue);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE)){
            camera.moveTop(moveValue);
            moveDirectionState = MoveDirectionState.JUMP;
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_Z)){
            camera.moveDown(moveValue);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_V)){

            isSprinting = !isSprinting;
        }
    }

    private void handleMoveWasd(){

        double moveMultiplier = isSprinting ? RUN_SENS : MOVE_SENS;
        double moveValue = deltaTimeInSeconds * moveMultiplier;

        if(eventsHandler.isKeyPressed(GLFW_KEY_W)){
            camera.moveForward(moveValue);
            moveDirectionState = MoveDirectionState.FRONT;
            isMoving = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_S)){

            camera.moveBack(moveValue * 0.5);
            moveDirectionState = MoveDirectionState.BACK;
            isMoving = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_A)){

            camera.moveLeft(moveValue);
            moveDirectionState = MoveDirectionState.LEFT;
            isMoving = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_D)){

            camera.moveRight(moveValue);
            moveDirectionState = MoveDirectionState.RIGHT;
            isMoving = true;
        }

        if(!isMoving) {
            return;
        }

        if(isSprinting){
            moveState = MoveState.RUN;
        }
        else{
            moveState = MoveState.WALK;
        }
    }

    private void handleMouseRotate(){

        double mouseXPosForWindowWidth = eventsHandler.getMouseXPosForWindowWidth();

        if(mouseXPosForWindowWidth == 0){
            return;
        }

        double moveValue = Math.abs(mouseXPosForWindowWidth) * ROTATION_SENS * deltaTimeInSeconds;

        if(mouseXPosForWindowWidth > 0){

            camera.rotateRight(moveValue);
        }
        else{

            camera.rotateLeft(moveValue);
        }

        eventsHandler.resetMouseMove();
    }

    private void updatePositionForCamera() {

        position = camera.getPosition().add(CAMERA_OFFSET);
        mesh.setModel(camera.getMatrixRelativeToCamera(CAMERA_OFFSET));
    }

    private void handleAttack(){

        int eventButtonId = eventsHandler.getEventButtonId();
        int buttonEventId = eventsHandler.getButtonEventId();

        if(eventButtonId == GLFW_MOUSE_BUTTON_1){

            if(buttonEventId == GLFW_PRESS){

                moveState = MoveState.STANDING;
                combatState = CombatState.FIGHTING;
            }
            else if(buttonEventId == GLFW_RELEASE){

                combatState = CombatState.NO_WEAPON;
            }
        }
    }

    @Override
    public void update(double deltaTimeInSeconds){

        moveState = MoveState.STANDING;
        moveDirectionState = MoveDirectionState.FRONT;
        combatState = CombatState.NO_WEAPON;
        isMoving = false;

        handleMove();
        handleMouseRotate();
        handleAttack();

        updatePositionForCamera();

        super.update(deltaTimeInSeconds);
    }
}
