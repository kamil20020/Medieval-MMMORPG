package pl.engine.mmorpg.texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AssimpTexture extends Texture{

    private final AIScene scene;
    private final AIVector3D.Buffer coords;

    public AssimpTexture(AIScene scene, AIMesh mesh){

        this.scene = scene;
        this.textureFileUrl = getTexturePath(scene, mesh);
        this.coords = mesh.mTextureCoords(0);

        ByteBuffer textureData = loadTextureData();
        this.textureId = createNonEmptyTexture(textureData);

        STBImage.stbi_image_free(textureData);
    }

    @Override
    protected ByteBuffer loadTextureData() throws IllegalStateException {

        if(textureFileUrl == null) {
            throw new IllegalStateException("Texture was not found");
        }

        ByteBuffer image = null;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        if(isTextureEmbedded(textureFileUrl)) {
            image = extractEmbeddedTexture(scene, textureFileUrl, width, height, comp);
        }
        else {
            image = extractOutsideTexture(textureFileUrl, width, height, comp);
        }

        if (image == null) {
            throw new IllegalStateException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }

        super.width = width.get(0);
        super.height = height.get(0);

        return image;
    }

    private static String getTexturePath(AIScene scene, AIMesh mesh){

        PointerBuffer materialsPtr = scene.mMaterials();
        long materialAddress = materialsPtr.get(mesh.mMaterialIndex());
        AIMaterial material = AIMaterial.create(materialAddress);

        PointerBuffer textures = scene.mTextures();

        for(int i = 0; i < scene.mNumTextures(); i++){

            long texAddress = textures.get(i);
            AITexture texture = AITexture.create(texAddress);

            if(texture.mHeight() == 0){

                return "*" + i;
            }
        }

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

    @Override
    public void appendUv(FloatBuffer buffer, int uvIndex){

        if(coords != null){
            AIVector3D uv = coords.get(uvIndex);
            buffer.put(uv.x());
            buffer.put(uv.y());
        }
        else{
            buffer.put(0f);
            buffer.put(0f);
        }
    }
}
