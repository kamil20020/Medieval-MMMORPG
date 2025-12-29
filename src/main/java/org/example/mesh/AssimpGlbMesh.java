package org.example.mesh;

import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import texture.AssimpTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class AssimpGlbMesh extends Mesh{

    protected AIVector3D.Buffer vertices;
    private AIVector3D.Buffer normals;
    private AIFace.Buffer faces;
    protected final AIMesh mesh;
    public List<Vector3f> realVertices = new ArrayList<>();

    public AssimpGlbMesh(AIMesh mesh, AssimpTexture texture) {

        this.mesh = mesh;
        this.texture = texture;

        this.vertices = mesh.mVertices();
        this.normals = mesh.mNormals();
        this.faces = mesh.mFaces();
        this.numberOfVertices = mesh.mNumVertices();
        this.numberOfFaces = mesh.mNumFaces();
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

    protected void appendVertex(FloatBuffer buffer, int vertexIndex){

        AIVector3D vertex = vertices.get(vertexIndex);
        buffer.put(vertex.x());
        buffer.put(vertex.y());
        buffer.put(vertex.z());
        realVertices.add(new Vector3f(vertex.x(), vertex.y(), vertex.z()));
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
