package org.example;

import org.example.mesh.ComplexGlbMesh;
import org.example.mesh.Meshable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Player implements Meshable {

    private Vector3f position;
    private final Meshable mesh;

    private final int modelId;
    private Matrix4f model = new Matrix4f().identity();
    private final FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer initModelBuffer;

    private final Camera camera;
    private final EventsHandler eventsHandler;

    private static final Vector3f CAMERA_OFFSET = new Vector3f(0, -2, 2);

    private static final double ROTATION_SENS = 2;
    private static final double MOVE_SENS = 0.1;

    public Player(Camera camera, int modelId, EventsHandler eventsHandler){
        mesh = new ComplexGlbMesh("models/warrior-sword.glb");
        this.modelId = modelId;
        this.camera = camera;
        this.eventsHandler = eventsHandler;

        eventsHandler.addKeyboardCallback(this::moveCallback);
        updatePositionForCamera();

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        Matrix4f identityMatrix = new Matrix4f().identity();
        initModelBuffer = identityMatrix.get(buffer);
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

        updatePositionForCamera();
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
        model = camera.getMatrixRelativeToCamera(CAMERA_OFFSET);
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();
    }

    @Override
    public void draw() {

        glUniformMatrix4fv(modelId, false, model.get(modelBuffer));
        mesh.draw();
        glUniformMatrix4fv(modelId, false, initModelBuffer);
    }

    @Override
    public void clear() {

        mesh.clear();
    }
}
