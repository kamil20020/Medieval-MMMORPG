package org.example.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import texture.GlbTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

public class AnimatedGlbMesh extends GlbMesh{

    private final List<List<Float>> verticesBonesWeights;
    private final List<List<Integer>> verticesBonesIndices;
    private final Map<String, Integer> bonesNamesIndices;

    public AnimatedGlbMesh(AIMesh mesh, GlbTexture texture, Map<String, Integer> bonesNamesIndices) {
        super(mesh, texture);

        verticesBonesWeights = new ArrayList<>();
        verticesBonesIndices = new ArrayList<>();

        for(int i = 0; i < mesh.mNumVertices(); i++){

            verticesBonesWeights.add(new ArrayList<>());
            verticesBonesIndices.add(new ArrayList<>());
        }

        this.bonesNamesIndices = bonesNamesIndices;

        loadBones();
    }

    private void loadBones(){

        PointerBuffer bones = mesh.mBones();

        for(int j = 0; j < mesh.mNumBones(); j++){

            long boneId = bones.get(j);
            AIBone bone = AIBone.create(boneId);
            String boneName = bone.mName().dataString();

            int boneIndex = bonesNamesIndices.get(boneName);

            AIVertexWeight.Buffer boneVerticesWeights = bone.mWeights();

            for(int k = 0; k < bone.mNumWeights(); k++){

                AIVertexWeight weight = boneVerticesWeights.get(k);
                int vertexIndex = weight.mVertexId();
                float weightValue = weight.mWeight();

                appendVertexBoneWeight(vertexIndex, weightValue);
                appendVertexBoneIndex(vertexIndex, boneIndex);
            }
        }

        for(int i = 0; i < verticesBonesIndices.size(); i++) {

            List<Integer> indices = verticesBonesIndices.get(i);
            List<Float> weights = verticesBonesWeights.get(i);

            if (weights.size() > 4) {
                verticesBonesWeights.set(i, weights.subList(0, 4));
                verticesBonesIndices.set(i, indices.subList(0, 4));
            }

            while (indices.size() < 4) indices.add(0);
            while (weights.size() < 4) weights.add(0f);
        }
    }

    private void appendVertexBoneWeight(int vertexIndex, float weight){

        List<Float> gotVertexBonesWeights = verticesBonesWeights.get(vertexIndex);
        gotVertexBonesWeights.add(weight);
    }

    private void appendVertexBoneIndex(int vertexIndex, int boneIndex) {

        List<Integer> gotVertexBonesIndices = verticesBonesIndices.get(vertexIndex);

        gotVertexBonesIndices.add(boneIndex);
    }

    @Override
    public void appendVertices(FloatBuffer buffer){

        super.appendVertices(buffer);

        IntBuffer boneIndicesBuffer = BufferUtils.createIntBuffer(numberOfVertices * 4);
        FloatBuffer boneWeightsBuffer = BufferUtils.createFloatBuffer(numberOfVertices * 4);

//        for (int i = 0; i < numberOfVertices; i++) {
//            List<Integer> indices = verticesBonesIndices.get(i);
//            List<Float> weights = verticesBonesWeights.get(i);
//
//            // dla max 4 kości na wierzchołek
//            for (int j = 0; j < 4; j++) {
//                boneIndicesBuffer.put(indices.get(j));
//                boneWeightsBuffer.put(weights.get(j));
//            }
//        }

        for (int i = 0; i < numberOfVertices; i++) {

            boneIndicesBuffer.put(0).put(0).put(0).put(0);

            boneWeightsBuffer.put(1f).put(0).put(0).put(0);
        }

        boneIndicesBuffer.flip();
        boneWeightsBuffer.flip();

        int vboBoneIndices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneIndices);
        glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
        glVertexAttribIPointer(2, 4, GL_INT, 4 * Integer.BYTES, 0);
        glEnableVertexAttribArray(2);

        int vboBoneWeights = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneWeights);
        glBufferData(GL_ARRAY_BUFFER, boneWeightsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(3);
    }
}
