package pl.engine.mmorpg.mesh.libraries.jgltf;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import pl.engine.mmorpg.mesh.Mesh;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.texture.JgltfTexture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class JgltfGlbMesh extends Mesh {

    private final MeshModel mesh;
    private IntBuffer indices;
    private FloatBuffer vertices;

    public JgltfGlbMesh(MeshModel mesh, JgltfTexture texture){

        this.mesh = mesh;
        this.texture = texture;

        loadVerticesData();
        loadFacesData();
    }

    private void loadVerticesData(){

        MeshPrimitiveModel primitive = mesh.getMeshPrimitiveModels().get(0);
        AccessorModel positionAccessor = primitive.getAttributes().get("POSITION");
        AccessorData gotPositionData = positionAccessor.getAccessorData();

        this.numberOfVertices = gotPositionData.getNumElements();
        this.vertices = gotPositionData.createByteBuffer().asFloatBuffer();
    }

    private void loadFacesData(){

        MeshPrimitiveModel primitive = mesh.getMeshPrimitiveModels().get(0);
        AccessorModel indicesAccessor = primitive.getIndices();
        AccessorData indicesData = indicesAccessor.getAccessorData();

        this.numberOfFaces = indicesData.getNumElements() / 3;

        Class<?> indicesDataType = indicesData.getComponentType();

        ByteBuffer externalBuffer = indicesData.createByteBuffer();

        if(indicesDataType == int.class || indicesDataType == Integer.class){

            IntBuffer externalIntBuffer = externalBuffer.asIntBuffer();

            indices = BufferUtils.createIntBuffer(externalIntBuffer.remaining());

            while(externalIntBuffer.hasRemaining()){

                indices.put(externalIntBuffer.get());
            }

            indices.flip();

            return;
        }

        ShortBuffer externalShortBuffer = externalBuffer.asShortBuffer();

        indices = BufferUtils.createIntBuffer(externalShortBuffer.remaining());

        while (externalShortBuffer.hasRemaining()) {

            int unsignedValue = Short.toUnsignedInt(externalShortBuffer.get());
            indices.put(unsignedValue);
        }

        indices.flip();
    }

    @Override
    public int getFaceNumberOfVertices(int faceIndex) {

        return 3;
    }

    @Override
    public IntBuffer getFaceVerticesBuffer(int faceIndex) {

        IntBuffer buffer = BufferUtils.createIntBuffer(3);

        int firstIndexIndex = faceIndex * 3;

        if (firstIndexIndex + 2 >= indices.capacity()) {
            throw new IllegalArgumentException("Face index out of bounds: " + firstIndexIndex);
        }

        buffer.put(indices.get(firstIndexIndex));
        buffer.put(indices.get(firstIndexIndex + 1));
        buffer.put(indices.get(firstIndexIndex + 2));
        buffer.flip();

        return buffer;
    }

    @Override
    public void appendVertices(FloatBuffer buffer) {

        for(int i = 0; i < numberOfVertices; i++){

            appendVertex(buffer, i);
            texture.appendUv(buffer, i);
        }
    }

    protected void appendVertex(FloatBuffer buffer, int vertexIndex){

        int firstVertexIndex = vertexIndex * 3;

        buffer.put(vertices.get(firstVertexIndex));
        buffer.put(vertices.get(firstVertexIndex + 1));
        buffer.put(vertices.get(firstVertexIndex + 2));
    }

    @Override
    public int getNumberOfVertices() {

        return numberOfVertices;
    }

    @Override
    public int getNumberOfFaces() {

        return numberOfFaces;
    }
}
