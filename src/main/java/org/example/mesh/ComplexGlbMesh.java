package org.example.mesh;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import texture.GlbTexture;

import java.util.ArrayList;
import java.util.List;

public class ComplexGlbMesh implements Meshable{

    private final List<GlbMesh> meshes = new ArrayList<>();

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

    private static AIScene loadScene(String modelPath) throws IllegalStateException{

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
}
