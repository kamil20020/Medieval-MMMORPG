package org.example.mesh;

import org.example.Renderer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

public abstract class AnimatedGlbMesh extends Mesh{

    protected final List<Matrix4f> bonesInverses = new ArrayList<>();
    protected final Map<String, Integer> bonesNamesIndices = new LinkedHashMap<>();
    protected final Matrix4f[] boneFinalTransformations;

    private double animationTime;
    protected double animationTicksPerSecond;
    protected double animationDurationInTicksPerSeconds;

    protected Matrix4f rootNodeGlobalInverseTransform;
    protected Matrix4f rootNodeParentNodeTransformation;

    protected final List<List<Float>> verticesBonesWeights;
    protected final List<List<Integer>> verticesBonesIndices;

    protected int numberOfBones;

    private final Mesh additionalMesh;

    protected static final Integer MAX_NUMBER_OF_BONES = 200;
    protected static final Integer MAX_NUMBER_OF_BONS_PER_VERTEX = 4;

    public AnimatedGlbMesh(Mesh additionalMesh){

        this.additionalMesh = additionalMesh;
        this.numberOfVertices = additionalMesh.numberOfVertices;
        this.texture = additionalMesh.texture;

        verticesBonesWeights = new ArrayList<>();
        verticesBonesIndices = new ArrayList<>();

        for(int i = 0; i < numberOfVertices; i++){

            verticesBonesWeights.add(new ArrayList<>());
            verticesBonesIndices.add(new ArrayList<>());
        }

        boneFinalTransformations = new Matrix4f[MAX_NUMBER_OF_BONES];

        for(int i = 0; i < MAX_NUMBER_OF_BONES; i++){

            boneFinalTransformations[i] = new Matrix4f().identity();
        }

//        printEmptyWeightsVertices();
//        printVerticesBonesWeights();
    }

    protected void initAnimation(){

        loadAnimationData();

        animationTicksPerSecond = animationTicksPerSecond > 0 ? animationTicksPerSecond : 25d;
    }

    public void updateAnimation(double deltaTimeInSeconds){

        animationTime += deltaTimeInSeconds * animationTicksPerSecond;
        animationTime %= animationDurationInTicksPerSeconds;

        loadFinalTransformation(animationTime);
    }

    @Override
    public void appendVertices(FloatBuffer buffer){

        additionalMesh.appendVertices(buffer);

        IntBuffer boneIndicesBuffer = BufferUtils.createIntBuffer(numberOfVertices * MAX_NUMBER_OF_BONS_PER_VERTEX);
        FloatBuffer boneWeightsBuffer = BufferUtils.createFloatBuffer(numberOfVertices * MAX_NUMBER_OF_BONS_PER_VERTEX);

        for (int i = 0; i < numberOfVertices; i++) {

            List<Integer> indices = verticesBonesIndices.get(i);
            List<Float> weights = verticesBonesWeights.get(i);

            for (int j = 0; j < MAX_NUMBER_OF_BONS_PER_VERTEX; j++) {

                boneIndicesBuffer.put(indices.get(j));
                boneWeightsBuffer.put(weights.get(j));
            }
        }

        boneIndicesBuffer.flip();
        boneWeightsBuffer.flip();

        int vboBoneIndices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneIndices);
        glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
        glVertexAttribIPointer(2, MAX_NUMBER_OF_BONS_PER_VERTEX, GL_INT, MAX_NUMBER_OF_BONS_PER_VERTEX * Integer.BYTES, 0);
        glEnableVertexAttribArray(2);

        int vboBoneWeights = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneWeights);
        glBufferData(GL_ARRAY_BUFFER, boneWeightsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(3, MAX_NUMBER_OF_BONS_PER_VERTEX, GL_FLOAT, false, MAX_NUMBER_OF_BONS_PER_VERTEX * Float.BYTES, 0);
        glEnableVertexAttribArray(3);
    }

    @Override
    public void draw() {

        glUniform1i(Renderer.isAnimatedId, 1);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16 * MAX_NUMBER_OF_BONES);

        for (int i = 0; i < MAX_NUMBER_OF_BONES; i++) {

            boneFinalTransformations[i].get(16 * i, buffer);
        }

        glUniformMatrix4fv(Renderer.finalBoneMatricesId, false, buffer);

        super.draw();

        glUniform1i(Renderer.isAnimatedId, 0);
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        super.update(deltaTimeInSeconds);

        updateAnimation(deltaTimeInSeconds);
    }

    @Override
    public int getFaceNumberOfVertices(int faceIndex) {

        return additionalMesh.getFaceNumberOfVertices(faceIndex);
    }

    @Override
    public IntBuffer getFaceVerticesBuffer(int faceIndex) {

        return additionalMesh.getFaceVerticesBuffer(faceIndex);
    }

    @Override
    public int getNumberOfVertices() {

        return additionalMesh.getNumberOfVertices();
    }

    @Override
    public int getNumberOfFaces() {

        return additionalMesh.getNumberOfFaces();
    }

    private void printFinal(){

        System.out.println("Submesh boneFinalTransformations:");

        for(int i = 0; i < numberOfBones; i++){

            String name = "";

            for(var m : bonesNamesIndices.entrySet()){

                if(i == m.getValue()){

                    name = m.getKey();
                    break;
                }
            }

            System.out.println(name + ": " + boneFinalTransformations[i]);
        }
    }

    private void printVerticesBones(){

        for (int i = 0; i < 100; i++) {

            System.out.println("Vertex " + i);
            System.out.println("  indices: " + verticesBonesIndices.get(i));
            System.out.println("  weights: " + verticesBonesWeights.get(i));
        }
    }

    protected void sortVerticesBones(){

        for(int i = 0; i < numberOfVertices; i++){

            List<Integer> indices = verticesBonesIndices.get(i);
            List<Float> weights = verticesBonesWeights.get(i);

            for(int j = 0; j < weights.size() - 1; j++){

                for(int k = 0; k < weights.size() - j - 1; k++){

                    if(weights.get(k) < weights.get(k + 1)){

                        float tempWeight = weights.get(k);
                        weights.set(k, weights.get(k + 1));
                        weights.set(k + 1, tempWeight);


                        int tempIndex = indices.get(k);
                        indices.set(k, indices.get(k + 1));
                        indices.set(k + 1, tempIndex);
                    }
                }
            }

            int maxBones = Math.min(MAX_NUMBER_OF_BONS_PER_VERTEX, weights.size());
            float sum = 0f;
            for (int j = 0; j < maxBones; j++) sum += weights.get(j);
            for (int j = 0; j < maxBones; j++) weights.set(j, weights.get(j) / sum);
        }
    }

    protected void normalizeVerticesWeightsAndIndices(){

        for(int i = 0; i < numberOfVertices; i++) {

            List<Integer> indices = verticesBonesIndices.get(i);
            List<Float> weights = verticesBonesWeights.get(i);

            while (indices.size() < MAX_NUMBER_OF_BONS_PER_VERTEX) indices.add(0);
            while (weights.size() < MAX_NUMBER_OF_BONS_PER_VERTEX) weights.add(0f);

            float sum = 0f;

            for (float w : weights) {

                sum += w;
            }

            if (sum > 0f) {

                for (int j = 0; j < weights.size(); j++) {

                    weights.set(j, weights.get(j) / sum);
                }
            }
            else {

                weights.set(0, 1f);
            }
        }
    }

    private void printEmptyWeightsVertices(){

        for(int i = 0; i < numberOfVertices; i++){

            if(verticesBonesWeights.get(i).size() == 0){
                System.out.println("Brak kości dla vertexa: " + i);
            }
        }
    }

    private void printVerticesBonesWeights(){

        for(int i = 0; i < numberOfVertices; i++){

            System.out.println(verticesBonesWeights.get(i));
        }
    }

    protected abstract void loadBones();
    protected abstract void loadAnimationData();
    protected abstract void loadFinalTransformation(double deltaTimeInSeconds);
}
