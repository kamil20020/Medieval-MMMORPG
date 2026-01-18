package pl.engine.mmorpg.mesh;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public abstract class Mesh implements Meshable{

    protected int vertexArraysId;
    protected int vertexBufferId;
    private int eboId;
    protected int numberOfVertices;
    protected int numberOfFaces;
    protected Texture texture;
    protected Matrix4f model = new Matrix4f().identity();
    private final FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
    protected double deltaTime;

    private static final int STRIDE = 5 * Float.BYTES;

    @Override
    public void uploadToGpu(){

        this.numberOfVertices = getNumberOfVertices();
        this.numberOfFaces = getNumberOfFaces();

        FloatBuffer buffer = loadVerticesBuffer();
        IntBuffer indicesBuffer = initIndicesBuffer();

        vertexArraysId = glGenVertexArrays();
        glBindVertexArray(vertexArraysId);

        bindVerticesBuffer(buffer);
        bindEboBuffer(indicesBuffer);

        model.get(modelBuffer);
    }

    private FloatBuffer loadVerticesBuffer(){

        //3 - x, y, z, 2 - uv texture, 3 - normals
        FloatBuffer buffer = BufferUtils.createFloatBuffer(numberOfVertices * 5);

        appendVertices(buffer);

        buffer.flip();

        return buffer;
    }

    private IntBuffer initIndicesBuffer(){

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(numberOfFaces * 3);

        for(int faceIndex = 0; faceIndex < numberOfFaces; faceIndex++){
            int faceNumberOfVertices = getFaceNumberOfVertices(faceIndex);

            if(faceNumberOfVertices != 3){
                System.out.println("Obsługiwane są tylko trójkąty, otrzymano " + faceNumberOfVertices + " wierzchołków");
                continue;
            }

            IntBuffer faceVerticesBuffer = getFaceVerticesBuffer(faceIndex);
            indicesBuffer.put(faceVerticesBuffer);
        }

        indicesBuffer.flip();

        return indicesBuffer;
    }

    private void bindVerticesBuffer(FloatBuffer buffer){

        vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, STRIDE, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    private void bindEboBuffer(IntBuffer indicesBuffer){

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    @Override
    public void draw(){

        if(texture != null){
            Texture.useTexture(texture.getId());
        }
        Shader shader = Shader.getInstance();
        shader.setPropertyValue(ShaderProps.MODEL, model);

        glBindVertexArray(vertexArraysId);
        glDrawElements(GL_TRIANGLES, getNumberOfFaces() * 3, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void update(double deltaTimeInSeconds){

        deltaTime = deltaTimeInSeconds;
    }

    @Override
    public void clear(){

        glDeleteBuffers(vertexBufferId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vertexArraysId);
    }

    @Override
    public void setModel(Matrix4f model){

        this.model = model;
    }

    public Texture getTexture(){

        return texture;
    }

    public int getVertexArraysId(){

        return vertexArraysId;
    }

    public abstract int getFaceNumberOfVertices(int faceIndex);
    public abstract IntBuffer getFaceVerticesBuffer(int faceIndex);
    public abstract void appendVertices(FloatBuffer buffer);
    public abstract int getNumberOfVertices();
    public abstract int getNumberOfFaces();
}
