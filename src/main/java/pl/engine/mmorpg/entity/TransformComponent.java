package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.Mesh;

public class TransformComponent implements Component{

    private Vector3f position = new Vector3f(-200, 10, -200);
    private Vector3f angle = new Vector3f(0, 0, 0);
    private ComplexMesh complexMesh;

    public TransformComponent(ComplexMesh complexMesh){

        this.complexMesh = complexMesh;
    }

    @Override
    public void update(double deltaTime) {

        Matrix4f model = new Matrix4f()
            .identity()
            .translate(position)
            .rotateY((float) -Math.toRadians(angle.y - 90));

        complexMesh.setModel(model);
    }

    public void move(Vector3f value){

        this.position.add(value);
    }

    public Vector3f getForward(){

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

    public Vector3f getPosition(){

        return position;
    }

    public Vector3f getAngle(){

        return new Vector3f(angle);
    }

    public void setAngle(Vector3f newAngle){

        this.angle = newAngle;
    }
}
