package pl.engine.mmorpg.entity.ui;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class UIElement {

    private final Vector2f bottomLeftCorner;
    private float width;
    private float height;
    private float textureU;
    private float textureV;
    protected Texture texture;

    private static final float[][] vertices = {
        {0f, 0f, -1},
        {0f, 1f, -1},
        {1f, 1f, -1},
        {1f, 0f, -1}
    };
    private static final int[][] faces = {
        {0, 2, 1},
        {3, 2, 0}
    };
    private static float[][] TEXTURE_COORDS = new float[][]{
        {0, 0},
        {0, 1},
        {1, 1},
        {1, 0}
    };

    public UIElement(Vector2f bottomLeftCorner, float width, float height, float textureU, float textureV){

        this.bottomLeftCorner = bottomLeftCorner;
        this.width = width;
        this.height = height;
        this.textureU = textureU;
        this.textureV = textureV;
    }

    public void uploadToGpu(FloatBuffer verticesBuffer, Texture texture){

        appendVertex(verticesBuffer, 0, bottomLeftCorner.x, bottomLeftCorner.y);
        appendVertex(verticesBuffer, 1, bottomLeftCorner.x, bottomLeftCorner.y + height);
        appendVertex(verticesBuffer, 2, bottomLeftCorner.x + width, bottomLeftCorner.y + height);
        appendVertex(verticesBuffer, 3, bottomLeftCorner.x + width, bottomLeftCorner.y);
    }

    private void appendVertex(FloatBuffer verticesBuffer, int vertexIndex, float offsetX, float offsetY){

        float[] vertex = vertices[vertexIndex];

        verticesBuffer.put(vertex[0] + offsetX);
        verticesBuffer.put(vertex[1] + offsetY);
        verticesBuffer.put(vertex[2]);
    }

    public static void appendIndices(IntBuffer indicesBuffer){

        IntBuffer face1Buffer = getFaceBuffer(0);
        IntBuffer face2Buffer = getFaceBuffer(1);

        indicesBuffer.put(face1Buffer);
        indicesBuffer.put(face2Buffer);
    }

    private static IntBuffer getFaceBuffer(int faceIndex){

        int[] indices = faces[faceIndex];
        IntBuffer faceBuffer = BufferUtils.createIntBuffer(3);

        faceBuffer.put(indices[0]);
        faceBuffer.put(indices[1]);
        faceBuffer.put(indices[2]);

        faceBuffer.flip();

        return faceBuffer;
    }

    public abstract void update(double deltaTime);
}
