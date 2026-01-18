package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.render.Camera;
import pl.engine.mmorpg.EventsHandler;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.render.Window;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Player extends Entity {

    private Vector3f position;

    private final Camera camera;
    private final EventsHandler eventsHandler;
    private MoveDirectionState moveDirectionState = MoveDirectionState.FRONT;
    private MoveState moveState = MoveState.STANDING;
    private CombatState combatState = CombatState.NO_WEAPON;
    private boolean isSprinting = true;
    private boolean isPressedButton = false;
    private double fightStartTime = 0;
    private double moveStopTime = 0;
    private boolean isMoving = false;
    private double mouseXDiff = 0;
    private static final double RUN_SENS = 6;

    private static final Vector3f CAMERA_OFFSET = new Vector3f(0, -2, 2.5f);

    public Player(Camera camera, EventsHandler eventsHandler, MeshAbstractFactory meshFactory){
        super("models/warrior.glb", getAnimationNamesPathsMappings(), MoveState.STANDING.name(), meshFactory);

        this.camera = camera;
        this.eventsHandler = eventsHandler;

//        eventsHandler.addMousePosCallback(this::mosePosCallback);
        eventsHandler.addMouseClickCallback(this::mouseClickCallback);

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

    private static String getKey(MoveState moveState){

        return moveState.name();
    }

    private static String getKey(MoveDirectionState moveDirectionState){

        return moveDirectionState.name();
    }

    private static String getKey(CombatState combatState){

        return combatState.name();
    }

    private static String getKey(MoveState moveState, MoveDirectionState moveDirectionState){

        return moveState.name() + "_" + moveDirectionState.name();
    }

    private void handleMove(){

        if(combatState == CombatState.FIGHTING){
            return;
        }

        actualAnimationName = getKey(MoveState.STANDING);
        moveState = MoveState.STANDING;

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

        updatePositionForCamera();
    }

    private void handleMoveWasd(){

        double moveMultiplier = isSprinting ? RUN_SENS : MOVE_SENS;
        double moveValue = deltaTimeInSeconds * moveMultiplier;
        isMoving = false;
        isPressedButton = false;

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

        updatePositionForCamera();
    }

    private void mosePosCallback(){

        if(eventsHandler.isMouseIdle()){
            return;
        }

        double xScaleForWindow = eventsHandler.getMouseXScaleForWindowWidth();

        if(xScaleForWindow == 0){
            return;
        }

        if(xScaleForWindow > 0){

            camera.rotateRight(Math.abs(xScaleForWindow) * ROTATION_SENS);
        }
        else{

            camera.rotateLeft(Math.abs(xScaleForWindow) * ROTATION_SENS);
        }

        mouseXDiff = xScaleForWindow;
    }

    private void updatePositionForCamera() {

        position = camera.getPosition().add(CAMERA_OFFSET);
        mesh.setModel(camera.getMatrixRelativeToCamera(CAMERA_OFFSET));
    }

    private void mouseClickCallback(int button, int action){

        if(button == GLFW_MOUSE_BUTTON_1){

            if(action == GLFW_PRESS){

                isPressedButton = true;

                moveState = MoveState.STANDING;
                combatState = CombatState.FIGHTING;
                actualAnimationName = getKey(combatState);
            }
            else if(action == GLFW_RELEASE){

                isPressedButton = false;
                fightStartTime = actualAnimation.getAnimationCompletion();
            }
        }
    }

    @Override
    public void update(double deltaTimeInSeconds){

        handleMove();
        mosePosCallback();

        if(combatState != CombatState.FIGHTING){

            if(moveState != MoveState.STANDING){

                actualAnimationName = getKey(moveState, moveDirectionState);
            }
            else {
                if(moveDirectionState == MoveDirectionState.JUMP){
                    actualAnimationName = getKey(moveDirectionState);
                }
                else{
                    actualAnimationName = getKey(moveState);
                }
            }
        }

//        if(!Objects.equals(actualAnimationName, nextAnimationName)){
//
//            if(!isBlending){
//
//                isBlending = true;
//                blendTime = glfwGetTime();
//            }
//            else{
//
//                double time = glfwGetTime();
//                double diff = time - blendTime;
//
//                double t = Math.min(diff / BLEND_DURATION, 1.0);
//                blend((float) t);
//
//                if(t >= 1.0){
//
//                    isBlending = false;
//                    actualAnimationName = nextAnimationName;
//                    blendTime = 0;
//                }
//            }
//        }

        super.update(deltaTimeInSeconds);
    }

    private void blend(float t){

        List<Matrix4f[]> actualFinals = actualAnimation.getFinalBones();
        List<Matrix4f[]> nextFinals = nextAnimation.getFinalBones();

        for(int i = 0; i < actualFinals.size(); i++){

            Matrix4f[] actualFinal = actualFinals.get(i);
            Matrix4f[] nextFinal = nextFinals.get(i);
            Matrix4f[] finals = new Matrix4f[actualFinal.length];

            for(int j = 0; j < actualFinal.length; j++){

                finals[j] = actualFinal[j].lerp(nextFinal[j], t);
            }

            AnimatedMesh actual = actualAnimation.getAnimatedMesh(i);
            actual.setFinals(finals);
        }
    }
}
