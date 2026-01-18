package pl.engine.mmorpg.mesh;

import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public abstract class ComplexMesh implements Meshable{

    public final List<Meshable> meshes = new ArrayList<>();

    public ComplexMesh(String complexModelFilePath){

        loadModel(complexModelFilePath);
    }

    protected ComplexMesh(){

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

    protected abstract void loadModel(String complexModelFilePath);
    public abstract Object getData();
}
