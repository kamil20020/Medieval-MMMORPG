package pl.engine.mmorpg.animation.libraries.assimp;

import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.libraries.assimp.AssimpGlbMesh;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import pl.engine.mmorpg.texture.AssimpTexture;

import java.util.*;

import static org.lwjgl.opengl.GL15.*;

public class AnimatedAssimpMesh extends AnimatedMesh {

    private AIAnimation animation;
    private AINode rootNode;
    private final AIMesh mesh;
    private final AIScene animatedScene;

    public AnimatedAssimpMesh(AIMesh modelMesh, AIScene animatedScene, AssimpTexture texture, Skeleton skeleton){
        super(new AssimpGlbMesh(modelMesh, texture), skeleton);

        this.animatedScene = animatedScene;
        this.mesh = modelMesh;

        loadBonesData();
//        sortVerticesBones();
        normalizeVerticesWeightsAndIndices();
        initAnimation();

//        for(int i = 0; i < 100; i++){
//
//            System.out.println(verticesBonesWeights.get(i));
//        }
    }

    @Override
    protected void loadBonesData(){

        PointerBuffer bones = mesh.mBones();

        for(int i = 0; i < mesh.mNumBones(); i++){

            long boneId = bones.get(i);
            AIBone bone = AIBone.create(boneId);
            String boneName = bone.mName().dataString();

            int boneIndex = skeleton.getBoneIndex(boneName);

            AIMatrix4x4 rawBoneOffset = bone.mOffsetMatrix();
            Matrix4f boneOffset = convert(rawBoneOffset);
            bonesInverses.set(boneIndex, boneOffset);

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

        PointerBuffer animations = animatedScene.mAnimations();
        long animationId = animations.get(0);
        animation = AIAnimation.create(animationId);

        rootNode = animatedScene.mRootNode();
//        Matrix4f rootGlobalTransformation = convert(rootNode.mTransformation());
//        rootNodeGlobalInverseTransform = rootGlobalTransformation.invert();
        rootNodeGlobalInverseTransform = new Matrix4f().identity();
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

        Matrix4f nodeTransformation = getNodeTransformation(node, animationTime);
        Matrix4f globalNodeTransformation = super.getGlobalTransformation(parentTransformation, nodeTransformation);

        super.loadFinalTransformation(nodeName, globalNodeTransformation);

        PointerBuffer nodeChildren = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++){

            long childNodeId = nodeChildren.get(i);
            AINode childNode = AINode.create(childNodeId);

            loadFinalNodesTransformations(childNode, globalNodeTransformation, animationTime);
        }
    }

    private Matrix4f getNodeTransformation(AINode node, double animationTime){

        String nodeName = node.mName().dataString();

        Optional<AINodeAnim> foundNodeAnimOpt = findNodeAnim(nodeName);
        Matrix4f nodeTransformation = convert(node.mTransformation());

        if (foundNodeAnimOpt.isPresent()){

            AINodeAnim foundNodeAnim = foundNodeAnimOpt.get();

            Vector3f translate = getTranslateInterpolated(foundNodeAnim, animationTime);
            Matrix4f translation = new Matrix4f().translation(translate);

            Quaternionf rotate = getRotationInterpolated(foundNodeAnim, animationTime);
            Matrix4f rotation = new Matrix4f().rotation(rotate);

            Vector3f scale = getScaleInterpolated(foundNodeAnim, animationTime);
            Matrix4f scaling = new Matrix4f().scaling(scale);

            nodeTransformation = new Matrix4f(translation)
                .mul(rotation)
                .mul(scaling);
        }

        return nodeTransformation;
    }

    private Optional<AINodeAnim> findNodeAnim(String boneName){

        PointerBuffer channels = animation.mChannels();

        for(int i = 0; i < animation.mNumChannels(); i++){

            long channelId = channels.get(i);
            AINodeAnim nodeAnim = AINodeAnim.create(channelId);
            String nodeAnimName = nodeAnim.mNodeName().dataString();

            if(Objects.equals(nodeAnimName, boneName)){

                return Optional.of(nodeAnim);
            }
        }

        return Optional.empty();
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

       return AnimatedMesh.getInterpolated(lessTimeVec, lessTime, moreTimeVec, moreTime, actualTimeInTicks);
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

        return AnimatedMesh.getInterpolated(lessTimeQuaternion, lessTime, moreTimeQuaternion, moreTime, actualTimeInTicks);
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

//        printVerticesAfterFinal();

//        drawDebugBones();
//        drawStaticBones();
    }

    @Override
    public float[] getVertices() {
        return new float[0];
    }

    @Override
    public int[] getFaces() {
        return new int[0];
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

        for (int i = 0; i < 10; i++) {

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
                transformed.mul(weight);

                animatedPos.add(transformed);
            }

            System.out.printf("Vertex %d: %s -> %s%n", i, pos, animatedPos);
        }
    }

    public void drawDebugBones() {

        Map<String, Vector3f> bonePositions = getAnimatedBonesPositions();

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

    public void drawStaticBones() {

        Map<String, Vector3f> bonePositions = getStaticBonePositions();

        glLineWidth(5.0f);
        glBegin(GL_LINES);

        drawNodeLines(rootNode, bonePositions);

        glEnd();
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
}
