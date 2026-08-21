package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class Entity implements Meshable {

    protected ComplexMesh mesh;
    protected Skeleton skeleton;

    protected EntityStateData entityStateData = new EntityStateData();

    private final List<Component> components = new ArrayList<>();

    protected double deltaTimeInSeconds = 0;

    public Entity(String modelPath, MeshAbstractFactory meshFactory){

        this.mesh = meshFactory.createComplexMesh(modelPath);
        this.skeleton = meshFactory.createSkeleton(mesh.getData());
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();

        doForAllComponents(Component::prepare);
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

        if(entityStateData.canActionBeInterrupted) {

            entityStateData.entityState = EntityState.STANDING;
        }

        doForAllComponents(Component::clear);
        doForAllComponents(Component::update, deltaTimeInSeconds);
        doForAllComponents(Component::save);

        this.deltaTimeInSeconds = deltaTimeInSeconds;
    }

    private void doForAllComponents(Consumer<Component> consumer){

        for(Component component : components){

            consumer.accept(component);
        }
    }

    private void doForAllComponents(BiConsumer<Component, Double> consumer, double deltaTimeInSeconds){

        for(Component component : components){

            consumer.accept(component, deltaTimeInSeconds);
        }
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

        return entityStateData.entityState;
    }

    protected void addComponents(List<Component> components){

        this.components.addAll(components);
    }
}
