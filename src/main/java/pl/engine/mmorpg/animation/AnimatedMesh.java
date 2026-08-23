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
import static org.lwjgl.opengl.GL30.*;

public abstract class AnimatedMesh extends Mesh {

    protected final List<Matrix4f> bonesInverses;
    protected Matrix4f[] boneFinalTransformations;

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

    protected float blendingProgress = 0;
    protected AnimatedMesh nextAnimation = null;

    private List<DynamicMesh> dynamicMeshes = new ArrayList<>();

    protected static final Integer MAX_NUMBER_OF_BONES = 200;
    protected static final Integer MAX_NUMBER_OF_BONS_PER_VERTEX = 4;

    public static final float DEFAULT_NUMBER_OF_TICS_PER_SECOND = 1;
    public static final float BLENDING_DURATION = 0.2f;

    public record NodeTransformation (

        Vector3f translation,
        Quaternionf rotation,
        Vector3f scaling
    ){}

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

        updateAnimationTime(deltaTimeInSeconds);

        if(nextAnimation != null){

            nextAnimation.updateAnimationTime(deltaTimeInSeconds);
        }

        loadFinalTransformation(animationTime);
        setDynamicMeshesModels(additionalMesh.getModel());
    }

    public void updateAnimationTime(double deltaTimeInSeconds){

        animationTime += deltaTimeInSeconds * animationTicksPerSecond;
        animationTime %= animationDurationInTicksPerSeconds;
    }

    protected Matrix4f getGlobalTransformation(Matrix4f parentTransformation, Matrix4f nodeTransformation){

        return new Matrix4f(parentTransformation).mul(nodeTransformation);
    }

    protected void loadFinalTransformation(String nodeName, Matrix4f globalTransformation){

        if(!skeleton.containsBone(nodeName)) {
            return;
        }

        int boneIndex = skeleton.getBoneIndex(nodeName);
        Matrix4f boneInverse = bonesInverses.get(boneIndex);

        boneFinalTransformations[boneIndex] = new Matrix4f(rootNodeGlobalInverseTransform)
            .mul(new Matrix4f(globalTransformation))
            .mul(boneInverse);
    }

    protected Matrix4f blendAnimations(
        NodeTransformation actualAnimationTransformation,
        NodeTransformation nextAnimationTransformation
    ){
        Vector3f translation1 = actualAnimationTransformation.translation();
        Quaternionf rotation1 = actualAnimationTransformation.rotation();
        Vector3f scaling1 = actualAnimationTransformation.scaling();

        Vector3f translation2 = nextAnimationTransformation.translation();
        Quaternionf rotation2 = nextAnimationTransformation.rotation();
        Vector3f scaling2 = nextAnimationTransformation.scaling();

        Vector3f combinedTranslation = translation1.lerp(translation2, blendingProgress, new Vector3f());
        Quaternionf combinedRotation = rotation1.slerp(rotation2, blendingProgress, new Quaternionf());
        Vector3f combinedScale = scaling1.lerp(scaling2, blendingProgress, new Vector3f());

        return getNodeTransformation(combinedTranslation, combinedRotation, combinedScale);
    }

    protected Matrix4f getNodeTransformation(NodeTransformation nodeTransformation){

        return getNodeTransformation(
            nodeTransformation.translation(),
            nodeTransformation.rotation(),
            nodeTransformation.scaling()
        );
    }

    protected Matrix4f getNodeTransformation(Vector3f translation, Quaternionf rotation, Vector3f scaling){

        return new Matrix4f().identity()
            .translate(translation)
            .rotate(rotation)
            .scale(scaling);
    }

    @Override
    public void uploadToGpu() {

        glBindVertexArray(additionalMesh.getVertexArraysId());

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
        glVertexAttribIPointer(3, MAX_NUMBER_OF_BONS_PER_VERTEX, GL_INT, MAX_NUMBER_OF_BONS_PER_VERTEX * Integer.BYTES, 0);
        glEnableVertexAttribArray(3);

        vboBoneWeights = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboBoneWeights);
        glBufferData(GL_ARRAY_BUFFER, boneWeightsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(4, MAX_NUMBER_OF_BONS_PER_VERTEX, GL_FLOAT, false, MAX_NUMBER_OF_BONS_PER_VERTEX * Float.BYTES, 0);
        glEnableVertexAttribArray(4);

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

        additionalMesh.draw();

        shader.setPropertyValue(ShaderProps.IS_ANIMATED, Boolean.FALSE);

        drawDynamicMeshes();
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

    @Override
    public void clear(){

        glDeleteBuffers(vboBoneIndices);
        glDeleteBuffers(vboBoneWeights);
    }

    public void reset(){

        animationTime = 0;
        nextAnimation = null;
        blendingProgress = 0;
    }

    public double getAnimationCompletion(){

        return animationTime / animationDurationInTicksPerSeconds;
    }

    public Matrix4f[] getBoneFinalTransformations(){

        return boneFinalTransformations;
    }

    public void setFinals(Matrix4f[] finals){

        boneFinalTransformations = finals;
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

    public void addDynamicMesh(DynamicMesh dynamicMesh){

        dynamicMeshes.add(dynamicMesh);
        dynamicMesh.uploadToGpu();
    }

    private void setDynamicMeshesModels(Matrix4f model){

        for (DynamicMesh dynamicMesh : dynamicMeshes){

            int boneIndex = skeleton.getBoneIndex(dynamicMesh.getBoneName());
            Matrix4f finalMatrix = boneFinalTransformations[boneIndex];

            Matrix4f finalModel = new Matrix4f(model)
                .mul(finalMatrix)
                .translate(dynamicMesh.getTranslation())
                .rotateXYZ(dynamicMesh.getRotation())
                .scale(dynamicMesh.getScale());

            dynamicMesh.setModel(finalModel);
        }
    }

    private void drawDynamicMeshes(){

        for(DynamicMesh dynamicMesh : dynamicMeshes){

            dynamicMesh.draw();
        }
    }

    public void setNextAnimation(AnimatedMesh nextAnimation){

        this.nextAnimation = nextAnimation;
    }

    public void setBlendingProgress(float blendingProgress){

        this.blendingProgress = blendingProgress;
    }

    protected boolean isBlending(){

        return nextAnimation != null;
    }

    protected abstract void loadBonesData();
    protected abstract void loadAnimationData();
    protected abstract void loadFinalTransformation(double deltaTimeInSeconds);
}
