package org.example.mesh;

import org.lwjgl.assimp.*;
import texture.GlbTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class GlbMesh extends Mesh{

    private AIVector3D.Buffer vertices;
    private AIVector3D.Buffer normals;
    private AIFace.Buffer faces;
    private final AIMesh mesh;

    public GlbMesh(AIMesh mesh, GlbTexture texture) {

        this.mesh = mesh;
        this.texture = texture;

        this.vertices = mesh.mVertices();
        this.normals = mesh.mNormals();
        this.faces = mesh.mFaces();
    }

    @Override
    public int getFaceNumberOfVertices(int faceIndex){

        AIFace foundFace = getFace(faceIndex);

        return foundFace.mNumIndices();
    }

    @Override
    public IntBuffer getFaceVerticesBuffer(int faceIndex){

        AIFace foundFace = getFace(faceIndex);

        return foundFace.mIndices();
    }

    private AIFace getFace(int faceIndex){

        return faces.get(faceIndex);
    }

    @Override
    public void appendVertices(FloatBuffer buffer){

        for(int i = 0; i < mesh.mNumVertices(); i++){

            appendVertex(buffer, i);
            texture.appendUv(buffer, i);
        }
    }

    private void appendVertex(FloatBuffer buffer, int vertexIndex){

        AIVector3D vertex = vertices.get(vertexIndex);
        buffer.put(vertex.x());
        buffer.put(vertex.y());
        buffer.put(vertex.z());
    }

    @Override
    public int getNumberOfVertices(){
        return mesh.mNumVertices();
    }

    @Override
    public int getNumberOfFaces(){
        return mesh.mNumFaces();
    }
}
