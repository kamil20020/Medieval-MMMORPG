package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.entity.animation.AnimationInfo;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.*;

public abstract class Entity implements Meshable {

    protected ComplexMesh mesh;
    protected Skeleton skeleton;

    protected EntityState entityState = EntityState.STANDING;
    protected EntityStateData entityStateData = new EntityStateData();

    private List<Component> components = new ArrayList<>();

    protected double deltaTimeInSeconds = 0;

    public Entity(String modelPath, MeshAbstractFactory meshFactory){

        this.mesh = meshFactory.createComplexMesh(modelPath);
        this.skeleton = meshFactory.createSkeleton(mesh.getData());
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();

        for(Component component : components){

            component.prepare();
        }
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

        for (Component component : components){

            component.clear();
        }
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        entityState = EntityState.STANDING;

        for (Component component : components){

            component.update(deltaTimeInSeconds);
        }

        this.deltaTimeInSeconds = deltaTimeInSeconds;
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

    public ComplexMesh getComplexMesh(){

        return mesh;
    }

    public double getDeltaTimeInSeconds(){

        return deltaTimeInSeconds;
    }

    public EntityState getEntityState(){

        return entityState;
    }

    protected void addComponents(List<Component> components){

        this.components.addAll(components);
    }
}
