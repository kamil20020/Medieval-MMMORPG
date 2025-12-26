package org.example.mesh;

import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import texture.GlbTexture;

import java.util.*;

public class ComplexGlbMesh implements Meshable{

    protected final List<GlbMesh> meshes = new ArrayList<>();

    public ComplexGlbMesh(String complexModelFilePath){

        AIScene aiScene = loadScene(complexModelFilePath);

        PointerBuffer meshesBuffer = aiScene.mMeshes();

        for(int i = 0; i < aiScene.mNumMeshes(); i ++){

            long meshId = meshesBuffer.get(i);
            AIMesh aiMesh = AIMesh.create(meshId);

            GlbTexture texture = new GlbTexture(aiScene, aiMesh);
            GlbMesh mesh = new GlbMesh(aiMesh, texture);

            meshes.add(mesh);
        }
    }

    public ComplexGlbMesh(){

    }

    public static AIScene loadScene(String modelPath) throws IllegalStateException{

        AIScene scene = Assimp.aiImportFile(
            modelPath,
            Assimp.aiProcess_Triangulate |
            Assimp.aiProcess_FlipUVs |
            Assimp.aiProcess_CalcTangentSpace
        );

        if (scene == null) {
            throw new IllegalStateException("Nie udało się wczytać modelu: " + Assimp.aiGetErrorString());
        }

        return scene;
    }

    @Override
    public void uploadToGpu(){

        for(GlbMesh mesh : meshes){
            mesh.uploadToGpu();
        }
    }

    @Override
    public void draw() {

        for(GlbMesh mesh : meshes){

            mesh.draw();
        }
    }

    @Override
    public void clear() {

        for(GlbMesh mesh : meshes){
            mesh.clear();
        }
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        for(GlbMesh mesh : meshes){

            mesh.update(deltaTimeInSeconds);
        }
    }

    @Override
    public void setModel(Matrix4f model) {

        for(GlbMesh mesh : meshes){
            mesh.setModel(model);
        }
    }
}
