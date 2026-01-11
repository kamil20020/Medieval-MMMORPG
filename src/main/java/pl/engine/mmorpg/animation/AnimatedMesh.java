package pl.engine.mmorpg.animation;

import pl.engine.mmorpg.mesh.Mesh;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

public abstract class AnimatedMesh extends Mesh {

    protected final List<Matrix4f> bonesInverses;
    protected final Matrix4f[] boneFinalTransformations;

    private double animationTime;
    protected double animationTicksPerSecond;
    protected double animationDurationInTicksPerSeconds;

    protected Matrix4f rootNodeGlobalInverseTransform;
    protected Matrix4f rootNodeParentNodeTransformation;

    protected final List<List<Float>> verticesBonesWeights;
    protected final List<List<Integer>> verticesBonesIndices;

    protected final Mesh additionalMesh;
    protected final Skeleton skeleton;

    private int vboBoneIndices;
    private int vboBoneWeights;

    protected static final Integer MAX_NUMBER_OF_BONES = 200;
    protected static final Integer MAX_NUMBER_OF_BONS_PER_VERTEX = 4;

    public AnimatedMesh(Mesh additionalMesh, Skeleton skeleton){

        this.additionalMesh = additionalMesh;
        this.skeleton = skeleton;
        this.numberOfVertices = additionalMesh.getNumberOfVertices();
        this.texture = additionalMesh.getTexture();

        this.bonesInverses = new ArrayList<>();

        for(int i = 0; i < skeleton.getNumberOfBones(); i++){

            bonesInverses.add(new Matrix4f().identity());
        }

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

    protected Matrix4f getGlobalTransformation(Matrix4f parentTransformation, Matrix4f nodeTransformation){

        return new Matrix4f(parentTransformation).mul(nodeTransformation);
    }

    protected void loadFinalTransformation(String nodeName, Matrix4f globalTransformation){

        if(skeleton.containsBone(nodeName)){

            int boneIndex = skeleton.getBoneIndex(nodeName);
            Matrix4f boneInverse = bonesInverses.get(boneIndex);

            boneFinalTransformations[boneIndex] = new Matrix4f(rootNodeGlobalInverseTransform)
                .mul(new Matrix4f(globalTransformation))
                .mul(boneInverse);
        }
    }

    @Override
    public void uploadToGpu() {

        super.uploadToGpu();

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

        vboBoneIndices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneIndices);
        glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
        glVertexAttribIPointer(2, MAX_NUMBER_OF_BONS_PER_VERTEX, GL_INT, MAX_NUMBER_OF_BONS_PER_VERTEX * Integer.BYTES, 0);
        glEnableVertexAttribArray(2);

        vboBoneWeights = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneWeights);
        glBufferData(GL_ARRAY_BUFFER, boneWeightsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(3, MAX_NUMBER_OF_BONS_PER_VERTEX, GL_FLOAT, false, MAX_NUMBER_OF_BONS_PER_VERTEX * Float.BYTES, 0);
        glEnableVertexAttribArray(3);

        glBindVertexArray(0);
    }

    @Override
    public void appendVertices(FloatBuffer buffer){

        additionalMesh.appendVertices(buffer);
    }

    @Override
    public void draw() {

        Shader shader = Shader.getInstance();

        shader.setPropertyValue(ShaderProps.IS_ANIMATED, Boolean.TRUE);

        shader.setPropertyValue(ShaderProps.FINAL_BONE_MATRICES, boneFinalTransformations);

        super.draw();

        shader.setPropertyValue(ShaderProps.IS_ANIMATED, Boolean.FALSE);
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

    public static Vector3f getInterpolated(Vector3f lessTimeVec, double lessTime, Vector3f moreTimeVec, double moreTime, double actualTimeInTicks){

        double timeDiff = moreTime - lessTime;
        if (timeDiff <= 0.000001) {
            return new Vector3f(lessTimeVec);
        }

        double factor = (actualTimeInTicks - lessTime) / timeDiff;
        factor = Math.max(0.0f, Math.min(1.0f, factor));

        return new Vector3f(lessTimeVec).lerp(new Vector3f(moreTimeVec), (float) factor);
    }

    public static Quaternionf getInterpolated(Quaternionf lessTimeQuaternion, double lessTime, Quaternionf moreTimeQuaternion, double moreTime, double actualTimeInTicks){

        double timeDiff = moreTime - lessTime;
        if (timeDiff <= 0.000001) {
            return new Quaternionf(lessTimeQuaternion);
        }

        double factor = (actualTimeInTicks - lessTime) / timeDiff;
        factor = Math.max(0.0f, Math.min(1.0f, factor));

        Quaternionf result = new Quaternionf(lessTimeQuaternion).slerp(new Quaternionf(moreTimeQuaternion), (float) factor);

        result.normalize();

        return result;
    }

    private void printFinal(){

        System.out.println("Submesh boneFinalTransformations:");

        for(int i = 0; i < skeleton.getNumberOfBones(); i++){

            String name = "";

            for(var m : skeleton.getEntrySet()){

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

    protected Map<String, Vector3f> getAnimatedBonesPositions() {

        Map<String, Vector3f> bonePositions = new LinkedHashMap<>();

        for (var entry : skeleton.getEntrySet()) {

            String name = entry.getKey();
            int index = entry.getValue();

            Vector3f pos = new Vector3f();
            boneFinalTransformations[index].getTranslation(pos);
            bonePositions.put(name, pos);
        }

        return bonePositions;
    }

    protected abstract void loadBonesData();
    protected abstract void loadAnimationData();
    protected abstract void loadFinalTransformation(double deltaTimeInSeconds);
}
