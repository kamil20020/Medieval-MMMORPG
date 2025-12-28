package org.example.mesh;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import texture.GlbTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;

public class AnimatedAssimpGlbMesh extends AnimatedGlbMesh{

    private AIAnimation animation;
    private AINode rootNode;
    private final AIMesh mesh;

    public AnimatedAssimpGlbMesh(AIMesh mesh, GlbTexture texture){
        super(new AssimpGlbMesh(mesh, texture));

        this.mesh = mesh;
        this.numberOfBones = mesh.mNumBones();

        loadBones();
//        sortVerticesBones();
        normalizeVerticesWeightsAndIndices();
        initAnimation();
    }

    @Override
    protected void loadBones(){

        PointerBuffer bones = mesh.mBones();

        for(int boneIndex = 0; boneIndex < mesh.mNumBones(); boneIndex++){

            long boneId = bones.get(boneIndex);
            AIBone bone = AIBone.create(boneId);
            String boneName = bone.mName().dataString();

            bonesNamesIndices.put(boneName, boneIndex);

            AIMatrix4x4 rawBoneOffset = bone.mOffsetMatrix();
            Matrix4f boneOffset = convert(rawBoneOffset);
            bonesInverses.add(boneOffset);

            AIVertexWeight.Buffer boneVerticesWeights = bone.mWeights();

            for(int weightIndex = 0; weightIndex < bone.mNumWeights(); weightIndex++){

                AIVertexWeight weight = boneVerticesWeights.get(weightIndex);
                int vertexIndex = weight.mVertexId();
                float weightValue = weight.mWeight();

                verticesBonesIndices.get(vertexIndex).add(boneIndex);
                verticesBonesWeights.get(vertexIndex).add(weightValue);
            }
        }
    }

    @Override
    protected void loadAnimationData(){

//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/warrior1-fight.glb");
        AIScene aiScene = ComplexAssimpGlbMesh.loadScene("animations/warrior-sword-fight.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/elo-animacja.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/elo-animacja-1.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/archer.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/lecimy1.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/test.fbx");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/fox.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/human.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/dragon.glb");
//        AIScene aiScene = ComplexGlbMesh.loadScene("animations/testowe.glb");

        PointerBuffer animations = aiScene.mAnimations();
        long animationId = animations.get(0);
        animation = AIAnimation.create(animationId);

        rootNode = aiScene.mRootNode();
        Matrix4f rootGlobalTransformation = convert(rootNode.mTransformation());
        rootNodeGlobalInverseTransform = rootGlobalTransformation.invert();
        rootNodeParentNodeTransformation = new Matrix4f().identity();

        animationTicksPerSecond = animation.mTicksPerSecond();
        animationDurationInTicksPerSeconds = animation.mDuration();
    }

    @Override
    protected void loadFinalTransformation(double animationTime) {

        loadFinalNodesTransformations(rootNode, rootNodeParentNodeTransformation, animationTime);
    }

    private void loadFinalNodesTransformations(AINode node, Matrix4f parentTransformation, double animationTime){

        String nodeName = node.mName().dataString();

        AINodeAnim foundNodeAnim = findNodeAnim(nodeName);
        Matrix4f nodeTransformation = convert(node.mTransformation());

        if (foundNodeAnim != null){

            Vector3f translate = getTranslateInterpolated(foundNodeAnim, animationTime);
            Matrix4f translation = new Matrix4f().translation(translate);

            Quaternionf rotate = getRotationInterpolated(foundNodeAnim, animationTime);
            Matrix4f rotation = new Matrix4f().rotation(rotate);

            Vector3f scale = getScaleInterpolated(foundNodeAnim, animationTime);
            Matrix4f scaling = new Matrix4f().scaling(scale);

            nodeTransformation = translation
                .mul(rotation)
                .mul(scaling);
        }

        Matrix4f globalNodeTransformation = new Matrix4f(parentTransformation).mul(nodeTransformation);

        if (bonesNamesIndices.containsKey(nodeName)) {

            int boneIndex = bonesNamesIndices.get(nodeName);

            Matrix4f boneInverse = bonesInverses.get(boneIndex);

            boneFinalTransformations[boneIndex] =
                new Matrix4f(globalNodeTransformation)
                .mul(boneInverse);
        }

        PointerBuffer nodeChildren = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++){

            long childNodeId = nodeChildren.get(i);
            AINode childNode = AINode.create(childNodeId);

            loadFinalNodesTransformations(childNode, globalNodeTransformation, animationTime);
        }
    }

    private AINodeAnim findNodeAnim(String boneName){

        PointerBuffer channels = animation.mChannels();

        for(int i = 0; i < animation.mNumChannels(); i++){

            long channelId = channels.get(i);
            AINodeAnim nodeAnim = AINodeAnim.create(channelId);
            String nodeAnimName = nodeAnim.mNodeName().dataString();

            if(Objects.equals(nodeAnimName, boneName)){
                return nodeAnim;
            }
        }

        return null;
    }

    private Vector3f getTranslateInterpolated(AINodeAnim nodeAnim, double actualTimeInTicks){

        AIVectorKey.Buffer keys = nodeAnim.mPositionKeys();
        int numberOfKeys = nodeAnim.mNumPositionKeys();

        return getInterpolated(keys, numberOfKeys, actualTimeInTicks);
    }

    private Vector3f getScaleInterpolated(AINodeAnim nodeAnim, double actualTimeInTicks){

        AIVectorKey.Buffer keys = nodeAnim.mScalingKeys();
        int numberOfKeys = nodeAnim.mNumScalingKeys();

        return getInterpolated(keys, numberOfKeys, actualTimeInTicks);
    }

    private Vector3f getInterpolated(AIVectorKey.Buffer keys, int numberOfKeys, double actualTimeInTicks){

        if(numberOfKeys == 1){

            AIVector3D firstValue = keys.get(0).mValue();

            return new Vector3f(firstValue.x(), firstValue.y(), firstValue.z());
        }

        int lessTimeIndex = getInterpolatedTimeIndex(keys, numberOfKeys, actualTimeInTicks);

        AIVectorKey lessTimeKey = keys.get(lessTimeIndex);
        AIVector3D lessTimeRawVec = lessTimeKey.mValue();
        Vector3f lessTimeVec = new Vector3f(lessTimeRawVec.x(), lessTimeRawVec.y(), lessTimeRawVec.z());
        double lessTime = lessTimeKey.mTime();

        AIVectorKey moreTimeKey = keys.get(lessTimeIndex + 1);
        AIVector3D moreTimeRawVec = moreTimeKey.mValue();
        Vector3f moreTimeVec = new Vector3f(moreTimeRawVec.x(), moreTimeRawVec.y(), moreTimeRawVec.z());
        double moreTime = moreTimeKey.mTime();

        double timeDiff = moreTime - lessTime;

        double factor = (actualTimeInTicks - lessTime) / timeDiff;

        return new Vector3f(lessTimeVec).lerp(new Vector3f(moreTimeVec), (float) factor);
    }

    private Quaternionf getRotationInterpolated(AINodeAnim nodeAnim, double actualTimeInTicks){

        AIQuatKey.Buffer keys = nodeAnim.mRotationKeys();
        int numberOfKeys = nodeAnim.mNumRotationKeys();

        if(numberOfKeys == 1){

            AIQuaternion firstValue = keys.get(0).mValue();

            return new Quaternionf(firstValue.x(), firstValue.y(), firstValue.z(), firstValue.w());
        }

        int lessTimeIndex = getInterpolatedTimeIndex(keys, numberOfKeys, actualTimeInTicks);

        AIQuatKey lessTimeKey = keys.get(lessTimeIndex);
        AIQuaternion rawLessTimeQuaternion = lessTimeKey.mValue();
        Quaternionf lessTimeQuaternion = new Quaternionf(
            rawLessTimeQuaternion.x(),
            rawLessTimeQuaternion.y(),
            rawLessTimeQuaternion.z(),
            rawLessTimeQuaternion.w()
        );
        double lessTime = lessTimeKey.mTime();

        AIQuatKey moreTimeKey = keys.get(lessTimeIndex + 1);
        AIQuaternion rawMoreTimeQuaternion = moreTimeKey.mValue();
        Quaternionf moreTimeQuaternion = new Quaternionf(
            rawMoreTimeQuaternion.x(),
            rawMoreTimeQuaternion.y(),
            rawMoreTimeQuaternion.z(),
            rawMoreTimeQuaternion.w()
        );

        double moreTime = moreTimeKey.mTime();

        double timeDiff = moreTime - lessTime;

        double factor = (actualTimeInTicks - lessTime) / timeDiff;

        Quaternionf result = new Quaternionf(lessTimeQuaternion).slerp(new Quaternionf(moreTimeQuaternion), (float) factor);

        result.normalize();

        return result;

    }

    private int getInterpolatedTimeIndex(AIQuatKey.Buffer keys, int numberOfKeys, double actualTimeInTicks){

        for(int i = 0; i < numberOfKeys - 1; i++){

            AIQuatKey laterKey = keys.get(i + 1);
            double laterKeyTime = laterKey.mTime();

            if(actualTimeInTicks < laterKeyTime){
                return i;
            }
        }

        return 0;
    }

    private int getInterpolatedTimeIndex(AIVectorKey.Buffer keys, int numberOfKeys, double actualTimeInTicks){

        for(int i = 0; i < numberOfKeys - 1; i++){

            AIVectorKey laterKey = keys.get(i + 1);
            double laterKeyTime = laterKey.mTime();

            if(actualTimeInTicks < laterKeyTime){
                return i;
            }
        }

        return 0;
    }

    @Override
    public void draw() {

        super.draw();

        drawDebugBones();
        drawStaticBones();
    }

    private Matrix4f convert(AIMatrix4x4 ai){

        return new Matrix4f(
            ai.a1(), ai.b1(), ai.c1(), ai.d1(),
            ai.a2(), ai.b2(), ai.c2(), ai.d2(),
            ai.a3(), ai.b3(), ai.c3(), ai.d3(),
            ai.a4(), ai.b4(), ai.c4(), ai.d4());
    }

    private void printVerticesAfterFinal(){

        AIVector3D.Buffer vertices = mesh.mVertices();
        List<Vector3f> vertexPositions = new ArrayList<>();

        for (int i = 0; i < mesh.mNumVertices(); i++) {

            AIVector3D v = vertices.get(i);
            vertexPositions.add(new Vector3f(v.x(), v.y(), v.z()));
        }

        for (int i = 0; i < numberOfVertices; i++) {

            Vector4f pos = new Vector4f(vertexPositions.get(i), 1.0f); // Twoja lista pozycji wierzchołków
            Vector4f animatedPos = new Vector4f(0, 0, 0, 0);

            List<Integer> boneIndices = verticesBonesIndices.get(i);
            List<Float> boneWeights = verticesBonesWeights.get(i);

            for (int j = 0; j < MAX_NUMBER_OF_BONS_PER_VERTEX; j++) {

                int boneID = boneIndices.get(j);
                float weight = boneWeights.get(j);
                Matrix4f boneMatrix = boneFinalTransformations[boneID];

                Vector4f transformed = new Vector4f();
                boneMatrix.transform(pos, transformed);
//                transformed.mul(weight);
                animatedPos.add(transformed);
            }

            System.out.printf("Vertex %d: %s -> %s%n", i, pos, animatedPos);
        }
    }

    private Map<String, Vector3f> getBoneWorldPositions() {
        Map<String, Vector3f> bonePositions = new LinkedHashMap<>();
        for (var entry : bonesNamesIndices.entrySet()) {
            String name = entry.getKey();
            int index = entry.getValue();

            Vector3f pos = new Vector3f();
            boneFinalTransformations[index].getTranslation(pos);
            bonePositions.put(name, pos);
        }
        return bonePositions;
    }

    public void drawDebugBones() {

        Map<String, Vector3f> bonePositions = getBoneWorldPositions();

        glLineWidth(6.0f);
        glBegin(GL_LINES);

        drawNodeLines(rootNode, bonePositions);

        glEnd();
    }

    private void drawNodeLines(AINode node, Map<String, Vector3f> bonePositions) {
        String nodeName = node.mName().dataString();
        Vector3f parentPos = bonePositions.get(nodeName);

        PointerBuffer children = node.mChildren();
        for (int i = 0; i < node.mNumChildren(); i++) {
            long childId = children.get(i);
            AINode child = AINode.create(childId);

            Vector3f childPos = bonePositions.get(child.mName().dataString());
            if (parentPos != null && childPos != null) {
                glVertex3f(parentPos.x, parentPos.y, parentPos.z);
                glVertex3f(childPos.x, childPos.y, childPos.z);
            }

            drawNodeLines(child, bonePositions);
        }
    }

    private Map<String, Vector3f> getStaticBonePositions() {
        Map<String, Vector3f> positions = new LinkedHashMap<>();
        addStaticNodePositions(rootNode, new Matrix4f().identity(), positions);
        return positions;
    }

    private void addStaticNodePositions(AINode node, Matrix4f parentTransform, Map<String, Vector3f> positions) {
        String nodeName = node.mName().dataString();
        Matrix4f nodeTransform = convert(node.mTransformation());
        Matrix4f globalTransform = new Matrix4f(parentTransform).mul(nodeTransform);

        Vector3f pos = new Vector3f();
        globalTransform.getTranslation(pos);

        positions.put(nodeName, pos);

        PointerBuffer children = node.mChildren();
        for (int i = 0; i < node.mNumChildren(); i++) {
            long childId = children.get(i);
            AINode child = AINode.create(childId);
            addStaticNodePositions(child, globalTransform, positions);
        }
    }

    public void drawStaticBones() {
        Map<String, Vector3f> bonePositions = getStaticBonePositions();

        glLineWidth(5.0f);
        glBegin(GL_LINES);

        drawStaticNodeLines(rootNode, bonePositions);

        glEnd();
    }

    private void drawStaticNodeLines(AINode node, Map<String, Vector3f> positions) {
        String nodeName = node.mName().dataString();
        Vector3f parentPos = positions.get(nodeName);

        PointerBuffer children = node.mChildren();
        for (int i = 0; i < node.mNumChildren(); i++) {
            long childId = children.get(i);
            AINode child = AINode.create(childId);

            Vector3f childPos = positions.get(child.mName().dataString());
            if (parentPos != null && childPos != null) {
                glVertex3f(parentPos.x, parentPos.y, parentPos.z);
                glVertex3f(childPos.x, childPos.y, childPos.z);
            }

            drawStaticNodeLines(child, positions);
        }
    }
}
