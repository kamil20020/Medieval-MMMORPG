package pl.engine.mmorpg.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Camera {

    private final Vector3f angle;
    private final Vector3f eye;
    private final Vector3f destination;
    private final Vector3f top;
    private final Vector3f middle;

    private static final Vector3f CAMERA_OFFSET = new Vector3f(0, 2, 0);
    private static final float DESTINATION_SCALE = 50;
    private static final float EYE_DIRECTION_SCALE = 2.5f;

    public Camera(Vector3f position){

        this.eye = new Vector3f(position);
        this.angle = new Vector3f(0, 0, 0);
        this.top = new Vector3f(0, 1, 0);
        this.destination = new Vector3f(0, 0, 20);
        this.middle = new Vector3f(position);

        updateDestinationAndEye();
    }

    public Vector3f getRootPosition(){

        return new Vector3f(middle);
    }

    public void moveForward(double speed){

        moveInForward(speed);
    }

    public void moveBack(double speed){

        moveInForward(-speed);
    }

    public void moveTop(double speed){

        middle.y += speed;
        eye.y = middle.y;
        destination.y += speed;
    }

    public void moveDown(double speed){

        middle.y -= speed;
        eye.y = middle.y;
        destination.y -= speed;
    }

    public void moveLeft(double speed){

        Vector3f forward = getForward();

        forward.rotateY((float) Math.toRadians(90));

        moveInDirection(speed, forward);
    }

    public void moveRight(double speed){

        Vector3f forward = getForward();

        forward.rotateY((float) Math.toRadians(90));

        moveInDirection(-speed, forward);
    }

    public void rotateLeft(double angle){

        this.angle.y -= angle;
        this.angle.y %= 360;

        updateDestinationAndEye();
    }

    public void rotateRight(double angle){

        this.angle.y += angle;
        this.angle.y %= 360;

        updateDestinationAndEye();
    }

    public void rotateTop(double angle){

        this.angle.x += angle;
        this.angle.x = Math.max(-89, Math.min(89, this.angle.x));

        updateDestinationAndEye();
    }

    public void rotateDown(double angle){

        this.angle.x -= angle;
        this.angle.x = Math.max(-89, Math.min(89, this.angle.x));

        updateDestinationAndEye();
    }

    private void moveInForward(double scale){

        Vector3f forward = getForward();

        moveInDirection(scale, forward);
    }

    private void moveInDirection(double scale, Vector3f dir){

        middle.x += scale * dir.x;
        middle.y += scale * dir.y;
        middle.z += scale * dir.z;

        updateDestinationAndEye();
    }

    private Vector3f getForward(){

        float yawRad = (float)Math.toRadians(angle.y);
        Vector3f forward = new Vector3f(
            (float)Math.cos(yawRad),
            0,
            (float)Math.sin(yawRad)
        ).normalize();

        if (forward.lengthSquared() < 1e-6f) {

            forward.set(0, 0, -1);
        }
        else {
            forward.normalize();
        }

        return forward;
    }

    private void updateDestinationAndEye(){

        Vector3f direction = getDirection();
        Vector3f destinationScale = new Vector3f(direction).mul(DESTINATION_SCALE);
        Vector3f newDestination = new Vector3f(middle).add(destinationScale);
        destination.set(newDestination);

        Vector3f scaledDirection = new Vector3f(direction).mul(EYE_DIRECTION_SCALE);
        Vector3f newEye = new Vector3f(middle).sub(scaledDirection).add(CAMERA_OFFSET);
        eye.set(newEye);
    }

    private Vector3f getDirection(){

        Vector3f direction = new Vector3f();

        direction.x = (float) (Math.cos(Math.toRadians(angle.x)) * Math.cos(Math.toRadians(angle.y)));
        direction.y = (float) (Math.sin(Math.toRadians(angle.x)));
        direction.z = (float) (Math.cos(Math.toRadians(angle.x)) * Math.sin(Math.toRadians(angle.y)));

        if (direction.lengthSquared() < 1e-6f) {

            direction.set(0, 0, -1);
        }

        return direction.normalize();
    }

    public void update(){

        Matrix4f view = new Matrix4f().lookAt(
            eye, destination, top
        );

        Shader shader = Shader.getInstance();

        shader.setPropertyValue(ShaderProps.CAMERA, view);
    }

    public Matrix4f getMatrixRelativeToCamera(){

        return new Matrix4f()
            .identity()
            .translate(middle)
            .rotateY((float) -Math.toRadians(angle.y - 90));
    }
}
