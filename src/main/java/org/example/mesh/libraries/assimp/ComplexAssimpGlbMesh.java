package org.example.mesh.libraries.assimp;

import org.example.mesh.ComplexGlbMesh;
import org.example.mesh.Meshable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import texture.AssimpTexture;

public class ComplexAssimpGlbMesh extends ComplexGlbMesh {

    public ComplexAssimpGlbMesh(String complexModelFilePath){

        super(complexModelFilePath);
    }

    protected ComplexAssimpGlbMesh(){

    }

    @Override
    protected void loadModel(String complexModelFilePath){

        AIScene aiScene = loadScene(complexModelFilePath);

        PointerBuffer meshesBuffer = aiScene.mMeshes();

        for(int i = 0; i < aiScene.mNumMeshes(); i ++){

            long meshId = meshesBuffer.get(i);
            AIMesh aiMesh = AIMesh.create(meshId);

            AssimpTexture texture = new AssimpTexture(aiScene, aiMesh);
            Meshable mesh = new AssimpGlbMesh(aiMesh, texture);

            meshes.add(mesh);
        }
    }

    public static AIScene loadScene(String modelPath) throws IllegalStateException{

        AIScene scene = Assimp.aiImportFile(
            modelPath,
            Assimp.aiProcess_Triangulate |
            Assimp.aiProcess_FlipUVs |
            Assimp.aiProcess_CalcTangentSpace |
            Assimp.aiProcess_JoinIdenticalVertices |
            Assimp.aiProcess_LimitBoneWeights
//            | Assimp.aiProcess_MakeLeftHanded
//            | Assimp.aiProcess_FlipWindingOrder
//            Assimp.aiProcess_PreTransformVertices
//            Assimp.aiProcess_ValidateDataStructure | // wykrywa problemy w strukturze modelu
//            Assimp.aiProcess_FixInfacingNormals
        );

        if (scene == null) {
            throw new IllegalStateException("Nie udało się wczytać modelu: " + Assimp.aiGetErrorString());
        }

        return scene;
    }
}
