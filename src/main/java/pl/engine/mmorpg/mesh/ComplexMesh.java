package pl.engine.mmorpg.mesh;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ComplexMesh implements Meshable{

    public final List<Meshable> meshes = new ArrayList<>();
    private int numberOfVertices;
    private int numberOfFaces;

    public ComplexMesh(String complexModelFilePath){

        loadModel(complexModelFilePath);
        initMeshesInfo();
    }

    protected ComplexMesh(){

    }

    protected void initMeshesInfo(){

        for(Meshable mesh : meshes){

            numberOfVertices += mesh.getNumberOfVertices();
            numberOfFaces += mesh.getNumberOfFaces();
        }
    }

    @Override
    public void uploadToGpu(){

        for(Meshable mesh : meshes){

            mesh.uploadToGpu();
        }
    }

    @Override
    public void draw() {

        for(Meshable mesh : meshes){

            mesh.draw();
        }
    }

    @Override
    public void clear() {

        for(Meshable mesh : meshes){

            mesh.clear();
        }
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        for(Meshable mesh : meshes){

            mesh.update(deltaTimeInSeconds);
        }
    }

    @Override
    public void setModel(Matrix4f model) {

        for(Meshable mesh : meshes){

            mesh.setModel(model);
        }
    }

    @Override
    public int getNumberOfVertices() {

        return numberOfVertices;
    }

    @Override
    public float[] getVertices() {

        float[] vertices = new float[numberOfVertices * 3];

        int filledVertices = 0;

        for(Meshable mesh : meshes){

            float[] meshVertices = mesh.getVertices();
            System.arraycopy(meshVertices, 0, vertices, filledVertices, meshVertices.length);
            filledVertices += mesh.getNumberOfVertices();
        }

        return vertices;
    }

    @Override
    public int getNumberOfFaces() {

        return numberOfFaces;
    }

    @Override
    public int[] getFaces() {

        int[] faces = new int[numberOfFaces * 3];

        int filledVertices = 0;

        for(Meshable mesh : meshes){

            int[] meshFaces = mesh.getFaces();
            System.arraycopy(meshFaces, 0, faces, filledVertices, meshFaces.length);
            filledVertices += mesh.getNumberOfFaces();
        }

        return faces;
    }

    protected abstract void loadModel(String complexModelFilePath);
    public abstract Object getData();
}
