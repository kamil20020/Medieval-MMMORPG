package org.example.mesh;

import org.lwjgl.BufferUtils;
import texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Rect extends Mesh{

    private static float[][] vertices = {
        {-0.5f, -0.5f, 0}, //lewy przód,
        {0.5f, -0.5f, 0}, //prawy przód,
        {-0.5f, 0.5f, 0}, //lewy tył,
        {0.5f, 0.5f, 0} //prawy tył,
    };
    private static int[][] faces = {
        {0, 1, 3},// pierwszy trójkąt (lewy przód, prawy przód, prawy tył)
        {3, 2, 0}  // drugi trójkąt (prawy tył, lewy tył, lewy pr)
    };
    public static float[][] TEXTURE_COORDS = new float[][]{
        {0, 0},
        {1, 0},
        {1, 1},
        {0, 1}
    };
    IntBuffer[] facesVerticesBuffers = new IntBuffer[2];

    public Rect(Texture texture){

        this.texture = texture;

        for(int i = 0; i < facesVerticesBuffers.length; i++){

            facesVerticesBuffers[i] = BufferUtils.createIntBuffer(faces[i].length);
            facesVerticesBuffers[i].put(faces[i]);
            facesVerticesBuffers[i].flip();
        }
    }

    public Rect(){

        this(null);
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

        for (float[] v : vertices) {
            buffer.put(v);
        }
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
