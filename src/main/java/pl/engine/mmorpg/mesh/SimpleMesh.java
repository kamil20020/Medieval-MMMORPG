package pl.engine.mmorpg.mesh;

import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class SimpleMesh extends Mesh{

    private final float[][] vertices;
    private final int[][] faces;

    private final IntBuffer[] facesVerticesBuffers;

    public SimpleMesh(float[][] vertices, int[][] faces, Texture texture){

        this(vertices, faces);

        this.texture = texture;
    }

    public SimpleMesh(float[][] vertices, int[][] faces){

        this.vertices = vertices;
        this.faces = faces;
        this.facesVerticesBuffers = new IntBuffer[faces.length];

        for(int i = 0; i < facesVerticesBuffers.length; i++){

            facesVerticesBuffers[i] = BufferUtils.createIntBuffer(faces[i].length);
            facesVerticesBuffers[i].put(faces[i][0]);
            facesVerticesBuffers[i].put(faces[i][1]);
            facesVerticesBuffers[i].put(faces[i][2]);
            facesVerticesBuffers[i].flip();
        }
    }

    @Override
    public int getFaceNumberOfVertices(int faceIndex) {

        return faces[faceIndex].length;
    }

    @Override
    public IntBuffer getFaceVerticesBuffer(int faceIndex) {

        return facesVerticesBuffers[faceIndex];
    }

    @Override
    public void appendVertices(FloatBuffer buffer) {

        for (int i = 0; i < vertices.length; i++) {

            appendVertex(buffer, i);
            texture.appendUv(buffer, i);
            appendNormals(buffer);
        }
    }

    private void appendVertex(FloatBuffer buffer, int vertexIndex){

        float[] vertex = vertices[vertexIndex];

        buffer.put(vertex[0]);
        buffer.put(vertex[1]);
        buffer.put(vertex[2]);
    }

    private void appendNormals(FloatBuffer buffer){

        buffer.put(1);
        buffer.put(1);
        buffer.put(1);
    }

    @Override
    public int getNumberOfVertices() {

        return vertices.length;
    }

    @Override
    public int getNumberOfFaces() {

        return faces.length;
    }

    @Override
    public float[] getVertices() {

        float[] flattedVertices = new float[vertices.length * 3];

        int resultIndex = 0;

        for(float[] vertex : vertices){

            flattedVertices[resultIndex] = vertex[0];
            flattedVertices[resultIndex + 1] = vertex[1];
            flattedVertices[resultIndex + 2] = vertex[2];

            resultIndex += 3;
        }

        return flattedVertices;
    }

    @Override
    public int[] getFaces() {

        int[] flattedFaces = new int[faces.length * 3];

        int resultIndex = 0;

        for(int[] face : faces){

            flattedFaces[resultIndex] = face[0];
            flattedFaces[resultIndex + 1] = face[1];
            flattedFaces[resultIndex + 2] = face[2];

            resultIndex += 3;
        }

        return flattedFaces;
    }
}
