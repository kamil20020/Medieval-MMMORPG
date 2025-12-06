package org.example.mesh;

import org.example.JsonFileLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.stb.STBImage;
import texture.Texture;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GlbMesh{

    private final AIVector3D.Buffer vertices;
    private final AIVector3D.Buffer normals;
    private final AIVector3D.Buffer texCoords;
    private final AIMesh mesh;
    private int textureId;

    public GlbMesh(String modelPath) {
        AIScene scene = Assimp.aiImportFile(
            modelPath,
            Assimp.aiProcess_Triangulate |
            Assimp.aiProcess_FlipUVs |
            Assimp.aiProcess_CalcTangentSpace
        );

        if (scene == null) {
            System.err.println("Nie udało się wczytać modelu: " + Assimp.aiGetErrorString());
        }

        mesh = AIMesh.create(scene.mMeshes().get(0)); // pierwsza siatka
        vertices = mesh.mVertices();
        normals = mesh.mNormals();
        texCoords = mesh.mTextureCoords(0); // pierwszy UV set
        textureId = loadTexture(scene); //Texture.createTexture("textures/warrior.png");
    }

    public int loadTexture(AIScene scene){

        String texturePath = getTexturePath(scene);

        if(texturePath == null){
            throw new RuntimeException("Texture was not found " + texturePath);
        }

        ByteBuffer image = null;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        if(isTextureEmbedded(texturePath)) {
            image = extractEmbeddedTexture(scene, texturePath, width, height, comp);
        }
        else {
            image = extractOutsideTexture(texturePath, width, height, comp);
        }

        if (image == null) {
            throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }

        // Sprawdzamy rozmiar tekstury
        System.out.println("Texture loaded with dimensions: " + width.get(0) + "x" + height.get(0));

        int textureId = Texture.createEmptyTexture();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);

        STBImage.stbi_image_free(image);

        return textureId;
    }

    private String getTexturePath(AIScene scene){

        PointerBuffer materialsPtr = scene.mMaterials();
        int numMaterials = scene.mNumMaterials();
        long materialAddress = materialsPtr.get(mesh.mMaterialIndex());
        AIMaterial material = AIMaterial.create(materialAddress);

        AIString path = AIString.calloc();
        int result = Assimp.aiGetMaterialTexture(
            material,
            Assimp.aiTextureType_DIFFUSE,
            0,
            path,
            (IntBuffer) null,
            null,
            null,
            null,
            null,
            null
        );

        if (result == Assimp.aiReturn_SUCCESS) {
            return path.dataString();
        }

        path.free();

        return null;
    }

    private boolean isTextureEmbedded(String texturePath){
        return texturePath.startsWith("*");
    }

    private ByteBuffer extractEmbeddedTexture(AIScene scene, String texturePath, IntBuffer width, IntBuffer height, IntBuffer comp){

        int index = Integer.parseInt(texturePath.substring(1));
        long texPtrId = scene.mTextures().get(index);
        AITexture tex = AITexture.create(texPtrId);
        ByteBuffer image = tex.pcDataCompressed();
        STBImage.stbi_set_flip_vertically_on_load(false);

        return STBImage.stbi_load_from_memory(
            image,
            width,
            height,
            comp,
            4   // RGBA
        );
    }

    private ByteBuffer extractOutsideTexture(String texturePath, IntBuffer width, IntBuffer height, IntBuffer comp){

        STBImage.stbi_set_flip_vertically_on_load(true);

        return STBImage.stbi_load("resources/" + texturePath, width, height, comp, 4);
    }

    public int getNumberOfVertices(){
        return mesh.mNumVertices();
    }

    public int getNumberOfFaces(){
        return mesh.mNumFaces();
    }

    public AIVector3D.Buffer getVertices() {
        return vertices;
    }

    public AIVector3D.Buffer getTexCoords() {
        return texCoords;
    }

    public AIFace.Buffer getFaces(){
        return mesh.mFaces();
    }

    public int getTextureId() {
        return textureId;
    }
}
