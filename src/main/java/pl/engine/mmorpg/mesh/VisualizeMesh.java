package pl.engine.mmorpg.mesh;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VisualizeMesh extends Mesh{

    private final float[] vertices;
    private final Vector4f color;

    private static final int STRIDE = 3 * Float.BYTES;

    public VisualizeMesh(float[] vertices, Vector4f color){

        this.vertices = vertices;
        this.color = color;
    }

    @Override
    public void uploadToGpu() {

        this.numberOfVertices = getNumberOfVertices();
        this.numberOfFaces = getNumberOfFaces();

        FloatBuffer buffer = loadVerticesBuffer();

        vertexArraysId = glGenVertexArrays();
        glBindVertexArray(vertexArraysId);

        bindVerticesBuffer(buffer);
    }

    @Override
    protected FloatBuffer loadVerticesBuffer(){

        FloatBuffer buffer = BufferUtils.createFloatBuffer(numberOfVertices * 3);

        appendVertices(buffer);

        buffer.flip();

        return buffer;
    }

    @Override
    protected void bindVerticesBuffer(FloatBuffer buffer) {

        vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0);
        glEnableVertexAttribArray(0);
    }

    @Override
    public int getFaceNumberOfVertices(int faceIndex) {
        return 0;
    }

    @Override
    public IntBuffer getFaceVerticesBuffer(int faceIndex) {
        return null;
    }

    @Override
    public void appendVertices(FloatBuffer buffer) {

        for(int i = 0; i < numberOfVertices; i++){

            appendVertex(buffer, i);
        }
    }

    protected void appendVertex(FloatBuffer buffer, int vertexIndex){

        int firstVertexIndex = vertexIndex * 3;

        buffer.put(vertices[firstVertexIndex]);
        buffer.put(vertices[firstVertexIndex + 1]);
        buffer.put(vertices[firstVertexIndex + 2]);
    }

    @Override
    public int getNumberOfVertices() {

        return vertices.length / 3;
    }

    @Override
    public int getNumberOfFaces() {

        return 0;
    }

    @Override
    public float[] getVertices() {

        return vertices;
    }

    @Override
    public int[] getFaces() {

        return null;
    }

    @Override
    public void draw() {

        Shader shader = Shader.getInstance();
        shader.setPropertyValue(ShaderProps.MODEL, model);
        shader.setPropertyValue(ShaderProps.IS_GIVEN_COLOR, Boolean.TRUE);
        shader.setPropertyValue(ShaderProps.COLOR, color);

        glPointSize(10.0f);
        glBindVertexArray(vertexArraysId);
        glDrawArrays(GL_POINTS, 0, numberOfVertices);
        glBindVertexArray(0);

        shader.setPropertyValue(ShaderProps.IS_GIVEN_COLOR, Boolean.FALSE);
    }
}
