package pl.engine.mmorpg.animation;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.Meshable;

public class DynamicMesh implements Meshable{

    private final Meshable mesh;
    private String boneName;
    private Vector3f translation = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f scale = new Vector3f(1f, 1f, 1f);

    public DynamicMesh(Meshable mesh, String boneName){

        this.mesh = mesh;
        this.boneName = boneName;
    }

    public DynamicMesh(Meshable mesh){

        this.mesh = mesh;
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

    @Override
    public int getNumberOfVertices() {

        return mesh.getNumberOfVertices();
    }

    @Override
    public int getNumberOfFaces() {

        return mesh.getNumberOfFaces();
    }

    @Override
    public float[] getVertices() {

        return mesh.getVertices();
    }

    @Override
    public int[] getFaces() {

        return mesh.getFaces();
    }

    public String getBoneName(){

        return boneName;
    }

    public void setBoneName(String boneName){

        this.boneName = boneName;
    }

    public Vector3f getTranslation() {

        return translation;
    }

    public void setTranslation(Vector3f translation) {

        this.translation = translation;
    }

    public Vector3f getRotation() {

        return rotation;
    }

    public void setRotation(Vector3f rotation) {

        this.rotation.set(rotation);
    }

    public Vector3f getScale() {

        return scale;
    }

    public void setScale(float scale) {

        this.scale.set(scale);
    }
}
