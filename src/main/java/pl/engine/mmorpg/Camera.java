package pl.engine.mmorpg;

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

    public Camera(Vector3f position){

        this.eye = new Vector3f(position);
        this.angle = new Vector3f(0, 0, 0);
        this.top = new Vector3f(0, 1, 0);
        this.destination = new Vector3f(0, 0, 20);

        updateDestination();
    }

    public Vector3f getPosition(){

        return new Vector3f(eye);
    }

    public void moveForward(double speed){

        moveInForward(speed);
    }

    public void moveBack(double speed){

        moveInForward(-speed);
    }

    public void moveTop(double speed){

        eye.y += speed;
        destination.y += speed;
    }

    public void moveDown(double speed){

        eye.y -= speed;
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

        updateDestination();
    }

    public void rotateRight(double angle){

        this.angle.y += angle;
        this.angle.y %= 360;

        updateDestination();
    }

    public void rotateTop(double angle){

        this.angle.x += angle;
        this.angle.x = Math.max(-89, Math.min(89, this.angle.x));

        updateDestination();
    }

    public void rotateDown(double angle){

        this.angle.x -= angle;
        this.angle.x = Math.max(-89, Math.min(89, this.angle.x));

        updateDestination();
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

        Shader shader = Shader.getInstance();

        shader.setPropertyValue(ShaderProps.CAMERA, view);
    }

    public Matrix4f getMatrixRelativeToCamera(Vector3f offset){

        return new Matrix4f()
            .identity()
            .translate(eye)
            .rotateY((float) -Math.toRadians(angle.y - 90))
            .translate(offset);
    }

}
