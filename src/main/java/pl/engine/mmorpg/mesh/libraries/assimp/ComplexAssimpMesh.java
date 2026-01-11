package pl.engine.mmorpg.mesh.libraries.assimp;

import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.Meshable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import pl.engine.mmorpg.texture.AssimpTexture;

public class ComplexAssimpMesh extends ComplexMesh {

    public ComplexAssimpMesh(String complexModelFilePath){

        super(complexModelFilePath);
    }

    protected ComplexAssimpMesh(){

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
