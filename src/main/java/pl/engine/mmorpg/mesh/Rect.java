package pl.engine.mmorpg.mesh;

import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Rect extends Mesh{

    private static final float[][] vertices = {
        {-100, 0, -100}, // lewy przód,
        {100, 0, -100},  // prawy przód,
        {100, 0, 100},   // prawy tył,
        {-100, 0, 100}   // lewy tył,
    };
    /*
   3 ______ 2
     |   /|
     |  / |
     | /  |
   0 |/___| 1
     */
    private static final int[][] faces = {
        {0, 2, 1}, // pierwszy trójkąt (lewy przód, prawy tył, prawy przód)
        {3, 2, 0}  // drugi trójkąt (lewy tył, prawy tył, lewy przód)
    };
    public static float[][] TEXTURE_COORDS = new float[][]{
        {0, 50},
        {50, 50},
        {50, 0},
        {0, 0}
    };
    IntBuffer[] facesVerticesBuffers = new IntBuffer[2];

    public Rect(Texture texture){

        this.texture = texture;

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
        }
    }

    private void appendVertex(FloatBuffer buffer, int vertexIndex){

        float[] vertex = vertices[vertexIndex];

        buffer.put(vertex[0]);
        buffer.put(vertex[1]);
        buffer.put(vertex[2]);
    }

    @Override
    public int getNumberOfVertices() {

        return vertices.length;
    }

    @Override
    public int getNumberOfFaces() {

        return faces.length;
    }
}
