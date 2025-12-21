package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Camera {

    private final Vector3f angle;
    private final Vector3f eye;
    private final Vector3f destination;
    private final Vector3f top;
    private final EventsHandler eventsHandler;

    private static final double ROTATION_SENS = 0.2;
    private static final double MOVE_SENS = 0.01;

    public Camera(Vector3f position, EventsHandler eventsHandler){

        this.eventsHandler = eventsHandler;
        this.eye = new Vector3f(position);
        this.angle = new Vector3f(0, 90, 0);
        this.top = new Vector3f(0, 1, 0);
        this.destination = new Vector3f(0, 0, 20);

        eventsHandler.addKeyboardCallback(this::moveCallback);

        updateDestination();
    }

    public Vector3f getPosition(){

        return eye;
    }

    public void moveCallback(Set<Integer> pressedKeyboardKeys){

        handleWasd(pressedKeyboardKeys);

        double time = eventsHandler.getTime();

        if(pressedKeyboardKeys.contains(GLFW_KEY_Q)){

            Vector3f forward = getForward();

            forward.rotateY((float) Math.toRadians(90));

            moveInDirection(time * MOVE_SENS, forward);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_E)){

            Vector3f forward = getForward();

            forward.rotateY((float) Math.toRadians(90));

            moveInDirection(-time * MOVE_SENS, forward);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_UP)){

            angle.x += time * ROTATION_SENS;
            angle.x = Math.max(-89, Math.min(89, angle.x));

            updateDestination();
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_DOWN)){

            angle.x -= time * ROTATION_SENS;
            angle.x = Math.max(-89, Math.min(89, angle.x));

            updateDestination();
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_SPACE)){

            eye.y += time * MOVE_SENS;
            destination.y += time * MOVE_SENS;
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_Z)){

            eye.y -= time * MOVE_SENS;
            destination.y -= time * MOVE_SENS;
        }
    }

    private void handleWasd(Set<Integer> pressedKeyboardKeys){

        double time = eventsHandler.getTime();

        if(pressedKeyboardKeys.contains(GLFW_KEY_W)){

            moveInForward(time * MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_S)){

            moveInForward(-time * MOVE_SENS);
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_A)){

            angle.y -= time * ROTATION_SENS;
            angle.y %= 360;

            updateDestination();
        }

        if(pressedKeyboardKeys.contains(GLFW_KEY_D)){

            angle.y += time * ROTATION_SENS;
            angle.y %= 360;

            updateDestination();
        }
    }

    private void moveInForward(double scale){

        Vector3f forward = getForward();

        moveInDirection(scale, forward);
    }

    private void moveInDirection(double scale, Vector3f dir){

        eye.x += scale * dir.x;
        eye.y += scale * dir.y;
        eye.z += scale * dir.z;

        updateDestination();
    }

    private Vector3f getForward(){

        Vector3f forward = new Vector3f(destination).sub(eye);

        if (forward.lengthSquared() < 1e-6f) {

            forward.set(0, 0, -1);
        }
        else {
            forward.normalize();
        }

        return forward;
    }

    private void updateDestination(){

        Vector3f direction = new Vector3f();

        direction.x = (float) (Math.cos(Math.toRadians(angle.x)) * Math.cos(Math.toRadians(angle.y)));
        direction.y = (float) (Math.sin(Math.toRadians(angle.x)));
        direction.z = (float) (Math.cos(Math.toRadians(angle.x)) * Math.sin(Math.toRadians(angle.y)));

        if (direction.lengthSquared() < 1e-6f) {

            direction.set(0, 0, -1);
        }

        direction.normalize();

        destination.set(eye).add(new Vector3f(direction).mul(50));
    }

    public void update(){

        Matrix4f view = new Matrix4f().lookAt(
            eye, destination, top
        );

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        view.get(fb);

        glLoadMatrixf(fb);
    }

}
