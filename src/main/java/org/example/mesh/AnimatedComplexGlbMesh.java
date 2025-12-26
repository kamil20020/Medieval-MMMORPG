package org.example.mesh;

import org.example.Renderer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import texture.GlbTexture;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class AnimatedComplexGlbMesh extends ComplexGlbMesh{

    private final List<Matrix4f> bonesInverses = new ArrayList<>();
    private final Map<String, Integer> bonesNamesIndices = new HashMap<>();
    private Matrix4f[] boneFinalTransformations;

    private double animationTime;
    private double animationTicksPerSecond;
    private double animationDurationInTicksPerSeconds;

    private AIAnimation animation;
    private AINode rootNode;
    private Matrix4f rootNodeGlobalInverseTransform;
    private Matrix4f rootNodeParentNodeTransformation;

    public AnimatedComplexGlbMesh(String complexModelFilePath){

        AIScene aiScene = loadScene(complexModelFilePath);

        PointerBuffer meshesBuffer = aiScene.mMeshes();

        int numberOfBones = 0;

        for(int i = 0; i < aiScene.mNumMeshes(); i ++){

            long meshId = meshesBuffer.get(i);
            AIMesh aiMesh = AIMesh.create(meshId);

            numberOfBones = loadMeshBones(aiMesh, numberOfBones);

            GlbTexture texture = new GlbTexture(aiScene, aiMesh);
            GlbMesh mesh = new AnimatedGlbMesh(aiMesh, texture, bonesNamesIndices);

            meshes.add(mesh);
        }

        initAnimation();
    }

    private int loadMeshBones(AIMesh mesh, int startIndex){

        int meshNumberOfBones = mesh.mNumBones();

        PointerBuffer bones = mesh.mBones();

        for(int j = 0; j < meshNumberOfBones; j++) {

            long boneId = bones.get(j);
            AIBone bone = AIBone.create(boneId);
            String boneName = bone.mName().dataString();

            if(bonesNamesIndices.containsKey(boneName)){
                continue;
            }

            bonesNamesIndices.put(boneName, j + startIndex);

            AIMatrix4x4 rawBoneOffset = bone.mOffsetMatrix();
            Matrix4f boneOffset = convert(rawBoneOffset);
            bonesInverses.add(boneOffset);
        }

        return meshNumberOfBones;
    }

    private Matrix4f convert(AIMatrix4x4 raw){

        return new Matrix4f(
            raw.a1(), raw.b1(), raw.c1(), raw.d1(),
            raw.a2(), raw.b2(), raw.c2(), raw.d2(),
            raw.a3(), raw.b3(), raw.c3(), raw.d3(),
            raw.a4(), raw.b4(), raw.c4(), raw.d4()
        );
    }

    private void initAnimation(){

        AIScene aiScene = loadScene("animations/warrior-sword-fight.glb");

        PointerBuffer meshes = aiScene.mMeshes();

        int numberOfBones = 0;

        for(int i = 0; i < aiScene.mNumMeshes(); i++){

            AIMesh mesh = AIMesh.create(meshes.get(i));
            numberOfBones += mesh.mNumBones();
        }

        boneFinalTransformations = new Matrix4f[numberOfBones];

        for(int i = 0; i < numberOfBones; i++){
            boneFinalTransformations[i] = new Matrix4f().identity();
        }

        PointerBuffer animations = aiScene.mAnimations();
        long animationId = animations.get(0);
        animation = AIAnimation.create(animationId);

        rootNode = aiScene.mRootNode();
        Matrix4f rootGlobalTransformation = convert(rootNode.mTransformation());
        rootGlobalTransformation.set(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            rootGlobalTransformation.m30(), rootGlobalTransformation.m31(), rootGlobalTransformation.m32(), 1
        );
        rootNodeGlobalInverseTransform = rootGlobalTransformation.invert();
        rootNodeParentNodeTransformation = new Matrix4f().identity();

        animationTicksPerSecond = animation.mTicksPerSecond();
        animationTicksPerSecond = animationTicksPerSecond > 0 ? animationTicksPerSecond : 25d;

        animationDurationInTicksPerSeconds = animation.mDuration();
    }

    public void updateAnimation(double deltaTimeInSeconds){

        animationTime += deltaTimeInSeconds * animationTicksPerSecond;
        animationTime %= animationDurationInTicksPerSeconds;

        loadFinalNodesTransformations(rootNode, rootNodeParentNodeTransformation, animationTime);
    }

    private void loadFinalNodesTransformations(
        AINode node,
        Matrix4f parentTransformation,
        double actualTimeInTicks
    ){
        String boneName = node.mName().dataString();
        Matrix4f globalTransformation = getGlobalTransformation(node, parentTransformation, actualTimeInTicks);

        if(bonesNamesIndices.containsKey(boneName)){

            Matrix4f boneInverse = getBoneInverse(node);

            Matrix4f finalNodeTransformation = new Matrix4f(rootNodeGlobalInverseTransform)
                .mul(new Matrix4f(globalTransformation)
                .mul(boneInverse));

            int boneIndex = bonesNamesIndices.get(boneName);
            boneFinalTransformations[boneIndex] = finalNodeTransformation;
        }

        PointerBuffer nodeChildren = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++){

            long childNodeId = nodeChildren.get(i);
            AINode childNode = AINode.create(childNodeId);

            loadFinalNodesTransformations(childNode, globalTransformation, actualTimeInTicks);
        }
    }

    private Matrix4f getGlobalTransformation(AINode node, Matrix4f parentTransformation, double actualTimeInTicks){

        Matrix4f nodeTransformation = convert(node.mTransformation());
        AINodeAnim foundNodeAnim = findNodeAnim(node);

        if(foundNodeAnim != null){

            nodeTransformation = getNodeTransformationMatrix(foundNodeAnim, actualTimeInTicks);
        }

        return new Matrix4f(parentTransformation).mul(nodeTransformation);
    }

    private AINodeAnim findNodeAnim(AINode node){

        String searchNodeName = node.mName().dataString();

        PointerBuffer channels = animation.mChannels();

        for(int j = 0; j < animation.mNumChannels(); j++) {

            long nodeAnimId = channels.get(j);
            AINodeAnim nodeAnim = AINodeAnim.create(nodeAnimId);
            String nodeAnimName = nodeAnim.mNodeName().dataString();

            if(Objects.equals(nodeAnimName, searchNodeName)){
                return nodeAnim;
            }
        }

        return null;
    }

    private Matrix4f getBoneInverse(AINode node){

        String boneName = node.mName().dataString();
        int boneIndex = bonesNamesIndices.get(boneName);

        return bonesInverses.get(boneIndex);
    }

    private Matrix4f getNodeTransformationMatrix(AINodeAnim nodeAnim, double actualTimeInTicks){

        return new Matrix4f()
            .translate(getInterpolatedPosition(nodeAnim, actualTimeInTicks))
            .rotation(getInterpolatedRotation(nodeAnim, actualTimeInTicks))
            .scale(getInterpolatedScaling(nodeAnim, actualTimeInTicks));
    }

    private Vector3f getInterpolatedPosition(AINodeAnim nodeAnim, double actualTimeInTicks){

        AIVectorKey.Buffer positionKeys = nodeAnim.mPositionKeys();
        int numberOfKeys = nodeAnim.mNumPositionKeys();

        return getInterpolated(positionKeys, numberOfKeys, actualTimeInTicks);
    }

    private Vector3f getInterpolatedScaling(AINodeAnim nodeAnim, double actualTimeInTicks){

        AIVectorKey.Buffer scalingKeys = nodeAnim.mScalingKeys();
        int numberOfKeys = nodeAnim.mNumScalingKeys();

        return getInterpolated(scalingKeys, numberOfKeys, actualTimeInTicks);
    }

    private Vector3f getInterpolated(AIVectorKey.Buffer keys, int numberOfKeys, double actualTimeInTicks){

        if(numberOfKeys == 1){

            AIVectorKey firstKey = keys.get(0);

            return new Vector3f(
                firstKey.mValue().x(),
                firstKey.mValue().y(),
                firstKey.mValue().z()
            );
        }

        int interpolationKeyIndex = getInterpolationKeyIndex(keys, numberOfKeys, actualTimeInTicks);

        AIVectorKey lessTimeKey = keys.get(interpolationKeyIndex);
        double lessTime = lessTimeKey.mTime();

        AIVectorKey moreTimeKey = keys.get(interpolationKeyIndex + 1);
        double moreTime = moreTimeKey.mTime();

        Vector3f lessTimePoint = new Vector3f(
            lessTimeKey.mValue().x(),
            lessTimeKey.mValue().y(),
            lessTimeKey.mValue().z()
        );

        Vector3f moreTimePoint = new Vector3f(
            moreTimeKey.mValue().x(),
            moreTimeKey.mValue().y(),
            moreTimeKey.mValue().z()
        );

        float factor = getInterpolationTimeFactor(lessTime, moreTime, actualTimeInTicks);

        return lerp(lessTimePoint, moreTimePoint, factor);
    }

    private Quaternionf getInterpolatedRotation(AINodeAnim nodeAnim, double actualTimeInTicks){

        AIQuatKey.Buffer rotationsKeys = nodeAnim.mRotationKeys();
        int numberOfKeys = nodeAnim.mNumRotationKeys();

        if(numberOfKeys == 1){

            AIQuatKey firstKey = rotationsKeys.get(0);

            return new Quaternionf(
                firstKey.mValue().x(),
                firstKey.mValue().y(),
                firstKey.mValue().z(),
                firstKey.mValue().w()
            );
        }

        int interpolationKeyIndex = getInterpolationKeyIndex(rotationsKeys, numberOfKeys, actualTimeInTicks);

        AIQuatKey lessTimeKey = rotationsKeys.get(interpolationKeyIndex);
        double lessTime = lessTimeKey.mTime();

        AIQuatKey moreTimeKey = rotationsKeys.get(interpolationKeyIndex + 1);
        double moreTime = moreTimeKey.mTime();

        Quaternionf lessTimeQ = new Quaternionf(
            lessTimeKey.mValue().x(),
            lessTimeKey.mValue().y(),
            lessTimeKey.mValue().z(),
            lessTimeKey.mValue().w()
        );

        Quaternionf moreTimeQ = new Quaternionf(
            moreTimeKey.mValue().x(),
            moreTimeKey.mValue().y(),
            moreTimeKey.mValue().z(),
            moreTimeKey.mValue().w()
        );

        float factor = getInterpolationTimeFactor(lessTime, moreTime, actualTimeInTicks);

        return lessTimeQ.slerp(moreTimeQ, factor);
    }

    private float getInterpolationTimeFactor(double lessTime, double moreTime, double actualTimeInTicks){

        return (float) ((actualTimeInTicks - lessTime) / (moreTime - lessTime));
    }

    private int getInterpolationKeyIndex(AIQuatKey.Buffer valuesKeys, int numberOfValues, double actualTimeInTicks){

        for(int i = 0; i < numberOfValues - 1; i++){

            AIQuatKey potentialPrevKey = valuesKeys.get(i);
            double potentialLessTimeKey = potentialPrevKey.mTime();

            AIQuatKey potentialNextKey = valuesKeys.get(i + 1);
            double potentialMoreTimeKey = potentialNextKey.mTime();

            if(potentialLessTimeKey <= actualTimeInTicks && actualTimeInTicks < potentialMoreTimeKey){
                return i;
            }
        }

        return numberOfValues - 2;
    }

    private static Vector3f lerp(Vector3f a, Vector3f b, float t) {

        return new Vector3f(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t
        );
    }

    private int getInterpolationKeyIndex(AIVectorKey.Buffer valuesKeys, int numberOfValues, double actualTimeInTicks){

        for(int i = 0; i < numberOfValues - 1; i++){

            AIVectorKey potentialPrevKey = valuesKeys.get(i);
            double potentialLessTimeKey = potentialPrevKey.mTime();

            AIVectorKey potentialNextKey = valuesKeys.get(i + 1);
            double potentialMoreTimeKey = potentialNextKey.mTime();

            if(potentialLessTimeKey <= actualTimeInTicks && actualTimeInTicks < potentialMoreTimeKey){
                return i;
            }
        }

        return numberOfValues - 1;
    }

    @Override
    public void draw() {

        glUniform1i(Renderer.isAnimatedId, 1);

//        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
//
//        for (int i = 0; i < 200 && i < boneFinalTransformations.length; i++) {
//            FloatBuffer fb = BufferUtils.createFloatBuffer(16);
//            boneFinalTransformations[i].get(fb);
//            glUniformMatrix4fv(Renderer.finalBoneMatricesId + i, false, fb);
//        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16 * boneFinalTransformations.length);
        for (int i = 0; i < boneFinalTransformations.length; i++) {
            Matrix4f a = boneFinalTransformations[i];
            a.get(16 * i, buffer); // wstawia macierz w formacie kolumnowym do bufora
        }

        glUniformMatrix4fv(Renderer.finalBoneMatricesId, false, buffer);

        for(GlbMesh mesh : meshes){

            mesh.draw();
        }

        glUniform1i(Renderer.isAnimatedId, 0);
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        super.update(deltaTimeInSeconds);

        updateAnimation(deltaTimeInSeconds);
    }
}
