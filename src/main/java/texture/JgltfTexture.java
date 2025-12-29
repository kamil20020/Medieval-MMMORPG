package texture;

import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.v2.MaterialModelV2;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

public class JgltfTexture extends Texture{

    private final FloatBuffer coords;

    public JgltfTexture(MeshModel mesh){

        this.coords = findCoords(mesh);

        TextureModel firstMaterialTextureModel = findMeshTextureModel(mesh);

        ByteBuffer foundTextureData = extractData(firstMaterialTextureModel);
        this.textureId = createNonEmptyTexture(foundTextureData);

        STBImage.stbi_image_free(foundTextureData);
    }

    private FloatBuffer findCoords(MeshModel mesh){

        List<MeshPrimitiveModel> meshPrimitiveModels = mesh.getMeshPrimitiveModels();
        MeshPrimitiveModel firstMeshPrimitiveModel = meshPrimitiveModels.get(0);
        Map<String, AccessorModel> foundMaterialAttributes = firstMeshPrimitiveModel.getAttributes();

        AccessorModel uvAccessor = foundMaterialAttributes.get("TEXCOORD_0");
        AccessorData uvAccessorData = uvAccessor.getAccessorData();

        return uvAccessorData.createByteBuffer().asFloatBuffer();
    }

    private TextureModel findMeshTextureModel(MeshModel mesh){

        List<MeshPrimitiveModel> meshPrimitiveModels = mesh.getMeshPrimitiveModels();
        MeshPrimitiveModel firstMeshPrimitiveModel = meshPrimitiveModels.get(0);
        MaterialModelV2 firstMeshMaterial = (MaterialModelV2) firstMeshPrimitiveModel.getMaterialModel();

        return firstMeshMaterial.getBaseColorTexture();
    }

    private ByteBuffer extractData(TextureModel textureModel){

        ImageModel firstMaterialTextureImageModel = textureModel.getImageModel();
        ByteBuffer foundTextureData = firstMaterialTextureImageModel.getImageData();

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(false);

        if (!foundTextureData.isDirect()) {

            ByteBuffer direct = BufferUtils.createByteBuffer(foundTextureData.remaining());
            direct.put(foundTextureData).flip();
            foundTextureData = direct;
        }

        ByteBuffer gotData = STBImage.stbi_load_from_memory(
            foundTextureData,
            width,
            height,
            comp,
            4   // RGBA
        );

        if (gotData == null) {
            throw new IllegalStateException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }

        super.width = width.get(0);
        super.height = height.get(0);

        return gotData;
    }

    @Override
    public void appendUv(FloatBuffer buffer, int uvIndex) {

        uvIndex = uvIndex * 2;

        if(coords != null){

            float u = coords.get(uvIndex);
            float v = coords.get(uvIndex + 1);

            buffer.put(u);
            buffer.put(v);
        }
        else{
            buffer.put(0f);
            buffer.put(0f);
        }
    }
}
