package pl.engine.mmorpg.entity.ui.element;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface UiElement {

    float[][] vertices = {
        {0f, 0f, -1},
        {0f, 1f, -1},
        {1f, 1f, -1},
        {1f, 0f, -1}
    };
    int[][] faces = {
        {0, 2, 1},
        {3, 2, 0}
    };

    void appendVertices(FloatBuffer verticesBuffer, Texture texture);
    void setParentOffset(Vector2f offset);
    void update(double deltaTime);

    static void appendIndices(IntBuffer indicesBuffer, int offset){

        IntBuffer face1Buffer = getFaceBuffer(0, offset);
        IntBuffer face2Buffer = getFaceBuffer(1, offset);

        indicesBuffer.put(face1Buffer);
        indicesBuffer.put(face2Buffer);
    }

    static IntBuffer getFaceBuffer(int faceIndex, int offset){

        int[] indices = faces[faceIndex];
        IntBuffer faceBuffer = BufferUtils.createIntBuffer(3);

        faceBuffer.put(indices[0] + offset);
        faceBuffer.put(indices[1] + offset);
        faceBuffer.put(indices[2] + offset);

        faceBuffer.flip();

        return faceBuffer;
    }
}
