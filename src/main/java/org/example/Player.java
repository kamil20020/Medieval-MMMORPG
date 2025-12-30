package org.example;

import org.example.mesh.Meshable;
import org.example.mesh.libraries.assimp.animation.AnimatedComplexAssimpGlbModel;
import org.example.mesh.libraries.jgltf.animation.AnimatedComplexJgltfGlbMesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Player implements Meshable {

    private Vector3f position;
    private Meshable mesh;

    private final Camera camera;
    private final EventsHandler eventsHandler;

    private static final Vector3f CAMERA_OFFSET = new Vector3f(0, -2, 2.5f);

    private static final double ROTATION_SENS = 2;
    private static final double MOVE_SENS = 0.1;

    public Player(Camera camera, EventsHandler eventsHandler){
//        mesh = new ComplexGlbMesh("models/warrior-sword.glb");
        mesh = new AnimatedComplexJgltfGlbMesh(
            "animations/warrior-sword-fight.glb",
            "animations/warrior-sword-fight.glb"
        );
//        mesh = new AnimatedComplexJgltfGlbMesh(
//            "animations/dragon1.glb",
//            "animations/dragon1.glb"
//        );
//        mesh = new ComplexGlbMesh("animations/archer.glb");
//        mesh = new ComplexGlbMesh("animations/lecimy1.glb");
//        mesh = new ComplexGlbMesh("animations/test.fbx");
//        mesh = new ComplexGlbMesh("animations/fox.glb");
//        mesh = new ComplexGlbMesh("animations/human.glb");
//        mesh = new ComplexGlbMesh("animations/warrior1-fight.glb");
//        mesh = new AnimatedComplexGlbMesh("animations/test1.glb");
//        mesh = new AnimatedComplexGlbMesh("animations/warrior-standing-sword.glb");
//        mesh = new ComplexGlbMesh("animations/dragon.glb");
//        mesh = new ComplexGlbMesh("animations/testowe.glb");
//        mesh = new AnimatedComplexGlbMesh("animations/dragon1.glb");
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

//        position = camera.getPosition().add(CAMERA_OFFSET);
//        mesh.setModel(camera.getMatrixRelativeToCamera(CAMERA_OFFSET));
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();
    }

    @Override
    public void setModel(Matrix4f model) {

        mesh.setModel(model);
    }

    @Override
    public void draw() {

        mesh.draw();
    }

    @Override
    public void clear() {

        mesh.clear();
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        mesh.update(deltaTimeInSeconds);
    }
}
