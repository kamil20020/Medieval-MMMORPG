package pl.engine.mmorpg;

import pl.engine.mmorpg.mesh.Entity;
import org.joml.Vector3f;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Player extends Entity {

    private Vector3f position;

    private final Camera camera;
    private final EventsHandler eventsHandler;

    private static final Vector3f CAMERA_OFFSET = new Vector3f(0, -2, 2.5f);

    public Player(Camera camera, EventsHandler eventsHandler){

        this.camera = camera;
        this.eventsHandler = eventsHandler;

        eventsHandler.addKeyboardCallback(this::moveCallback);
        updatePositionForCamera();
    }

    public void moveCallback(Set<Integer> pressedKeyboardKeys){

        handleWasd(pressedKeyboardKeys);

        if(pressedKeyboardKeys.contains(GLFW_KEY_Q)){
            camera.moveLeft(MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_E)){
            camera.moveRight(MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_UP)){
           camera.rotateTop(ROTATION_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_DOWN)){
            camera.rotateDown(ROTATION_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_SPACE)){
            camera.moveTop(MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_Z)){
            camera.moveDown(MOVE_SENS);
        }

//        updatePositionForCamera();
    }

    private void handleWasd(Set<Integer> pressedKeyboardKeys){

        if(pressedKeyboardKeys.contains(GLFW_KEY_W)){
           camera.moveForward(MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_S)){
            camera.moveBack(MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_A)){
            camera.rotateLeft(ROTATION_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_D)){
            camera.rotateRight(ROTATION_SENS);
        }
    }

    private void updatePositionForCamera(){

        position = camera.getPosition().add(CAMERA_OFFSET);
        mesh.setModel(camera.getMatrixRelativeToCamera(CAMERA_OFFSET));
    }
}
