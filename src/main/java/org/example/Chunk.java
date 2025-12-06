package org.example;

import org.example.mesh.GlbMesh;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIVector3D;
import texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Chunk {

    private int vertexArraysId;
    private int vertexBufferId;
    private int eboId;
    private int numberOfVertices;
    private GlbMesh mesh;

    private static final int STRIDE = 5 * Float.BYTES;;

    public void init(){

        uploadToGpu();
    }

    private void uploadToGpu(){

        mesh = new GlbMesh("models/warrior.glb");

        numberOfVertices = mesh.getNumberOfVertices();

        FloatBuffer buffer = loadVerticesBuffer();
        IntBuffer indicesBuffer = initIndicesBuffer();

        vertexArraysId = glGenVertexArrays();
        glBindVertexArray(vertexArraysId);

        bindVerticesBuffer(buffer);
        bindEboBuffer(indicesBuffer);
    }

    private FloatBuffer loadVerticesBuffer(){

        //3 - x, y, z, 2 - uv texture, 3 - normals
        FloatBuffer buffer = BufferUtils.createFloatBuffer(numberOfVertices * 5);

        for(int i = 0; i < numberOfVertices; i++){

            appendVertex(buffer, mesh.getVertices(), i);
            appendUv(buffer, mesh.getTexCoords(), i);
        }

        buffer.flip();

        return buffer;
    }

    private void appendVertex(FloatBuffer buffer, AIVector3D.Buffer verticesBuffer, int vertexIndex){

        AIVector3D vertex = verticesBuffer.get(vertexIndex);
        buffer.put(vertex.x());
        buffer.put(vertex.y());
        buffer.put(vertex.z());
    }

    private void appendUv(FloatBuffer buffer, AIVector3D.Buffer uvBuffer, int uvIndex){

        if(uvBuffer != null){
            AIVector3D uv = uvBuffer.get(uvIndex);
            buffer.put(uv.x());
            buffer.put(uv.y());
        }
        else{
            buffer.put(0f);
            buffer.put(0f);
        }
    }

    private IntBuffer initIndicesBuffer(){

        int numberOfFaces = mesh.getNumberOfFaces();
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(numberOfFaces * 3);

        AIFace.Buffer facesBuffer = mesh.getFaces();

        for(int j = 0; j < numberOfFaces; j++){
            AIFace face = facesBuffer.get(j);

            if(face.mNumIndices() != 3){
                System.out.println("Obsługiwane są tylko trójkąty " + face);
                continue;
            }

            indicesBuffer.put(face.mIndices());
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

        glVertexAttribPointer(1, 3, GL_FLOAT, false, STRIDE, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    private void bindEboBuffer(IntBuffer indicesBuffer){

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    public void draw(){

        Texture.useTexture(mesh.getTextureId());
        glBindVertexArray(vertexArraysId);
        glDrawElements(GL_TRIANGLES, mesh.getNumberOfFaces() * 3, GL_UNSIGNED_INT, 0);
    }

    public void clear(){

        glDeleteBuffers(vertexBufferId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vertexArraysId);
    }
}
