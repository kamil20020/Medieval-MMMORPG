package pl.engine.mmorpg.mesh.libraries.jgltf.animation;

import de.javagl.jgltf.model.*;
import pl.engine.mmorpg.mesh.animation.AnimatedGlbMesh;
import pl.engine.mmorpg.mesh.animation.Skeleton;
import pl.engine.mmorpg.mesh.libraries.jgltf.JgltfGlbMesh;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pl.engine.mmorpg.texture.JgltfTexture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class AnimatedJgltfGlbMesh extends AnimatedGlbMesh {

    private final MeshModel mesh;
    private final NodeModel meshNode;
    private final GltfModel animatedModel;
    private NodeModel rootNode;
    private AnimationModel animation;

    private record ChannelTimeInterpolationData (

        int lessTimeIndex,
        double lessTime,
        double moreTime
    ){}

    private record ChannelVectorInterpolationData(

        Vector3f lessTimeVec,
        Vector3f moreTimeVec
    ){}

    private record ChannelQuaternionInterpolationData(

        Quaternionf lessTimeQuaternion,
        Quaternionf moreTimeQuaternion
    ){}

    private static class NodeChannels {

        public AnimationModel.Channel translateChannel;
        public AnimationModel.Channel rotationChannel;
        public AnimationModel.Channel scaleChannel;
    }

    public AnimatedJgltfGlbMesh(GltfModel animatedModel, MeshModel mesh, JgltfTexture texture, Skeleton skeleton) {
        super(new JgltfGlbMesh(mesh, texture), skeleton);

        this.mesh = mesh;
        this.animatedModel = animatedModel;
        this.meshNode = findMeshNodeModel(animatedModel, mesh.getName());

        loadBonesData();
//        sortVerticesBones();
        normalizeVerticesWeightsAndIndices();
        initAnimation();
    }

    private NodeModel findMeshNodeModel(GltfModel model, String meshName) throws IllegalStateException{

        for(var node : model.getNodeModels()){

            List<MeshModel> nodeMeshModels = node.getMeshModels();

            if(nodeMeshModels == null || nodeMeshModels.isEmpty()){
                continue;
            }

            MeshModel firstMeshModel = nodeMeshModels.get(0);
            String nodeMeshName = firstMeshModel.getName();

            if(Objects.equals(nodeMeshName, meshName)){

                return node;
            }
        }

        throw new IllegalStateException("Nie znaleziono węzła dla mesha " + mesh.getName());
    }

    @Override
    protected void loadBonesData() {

        loadVerticesBonesIndicesAndWeights();
        loadBonesInverses();
    }

    private void loadVerticesBonesIndicesAndWeights(){

        MeshPrimitiveModel meshPrimitiveModel = mesh.getMeshPrimitiveModels().get(0);

        AccessorData bonesIndicesDataAccessor = meshPrimitiveModel.getAttributes().get("JOINTS_0").getAccessorData();
        ByteBuffer bonesIndicesData = bonesIndicesDataAccessor.createByteBuffer();
        Class<?> bonedIndicesDataType = bonesIndicesDataAccessor.getComponentType();

        AccessorData bonesWeightsDataAccessor = meshPrimitiveModel.getAttributes().get("WEIGHTS_0").getAccessorData();
        FloatBuffer bonesWeightsData = bonesWeightsDataAccessor.createByteBuffer().asFloatBuffer();

        for(int vertexIndex = 0; vertexIndex < numberOfVertices; vertexIndex++){

            for(int j = 0; j < MAX_NUMBER_OF_BONS_PER_VERTEX; j++){

                int boneIndex = 0;

                if (bonedIndicesDataType == Byte.class || bonedIndicesDataType == byte.class) {
                    boneIndex = Byte.toUnsignedInt(bonesIndicesData.get());
                }
                else if (bonedIndicesDataType == Short.class || bonedIndicesDataType == short.class) {
                    boneIndex = Short.toUnsignedInt(bonesIndicesData.getShort());
                }
                else{
                    boneIndex = bonesIndicesData.get();
                }

                float boneWeight = bonesWeightsData.get();

                verticesBonesIndices.get(vertexIndex).add(boneIndex);
                verticesBonesWeights.get(vertexIndex).add(boneWeight);
            }
        }

        bonesIndicesData.flip();
        bonesWeightsData.flip();
    }

    public static boolean isAnimated(MeshModel mesh){

        for (MeshPrimitiveModel primitive : mesh.getMeshPrimitiveModels()) {

            if (primitive.getAttributes().containsKey("JOINTS_0")) {
                return true;
            }
        }

        return false;
    }

    private void loadBonesInverses(){

        SkinModel skinModel = meshNode.getSkinModel();

        AccessorModel dataAccessorModel = skinModel.getInverseBindMatrices();
        AccessorData dataAccessor = dataAccessorModel.getAccessorData();
        FloatBuffer data = dataAccessor.createByteBuffer().asFloatBuffer();

        for(int boneIndex = 0; boneIndex < skeleton.getNumberOfBones(); boneIndex++){

            float[] boneInverseData = new float[16];
            data.get(boneIndex * 16, boneInverseData);

            Matrix4f convertedBoneInverseData = new Matrix4f().set(boneInverseData);

            bonesInverses.set(boneIndex, convertedBoneInverseData);
        }
    }

    @Override
    protected void loadAnimationData() {

        SceneModel firstSceneModel = animatedModel.getSceneModels().get(0);
        rootNode = firstSceneModel.getNodeModels().get(0);
        rootNodeParentNodeTransformation = new Matrix4f().identity();

//        float[] rootGlobalData = new float[16];
//        rootNode.computeGlobalTransform(rootGlobalData);
//        rootNodeGlobalInverseTransform = new Matrix4f().set(rootGlobalData).invert();
        rootNodeGlobalInverseTransform = new Matrix4f().identity();

        animation = animatedModel.getAnimationModels().get(0);
        List<AnimationModel.Channel> channels = animation.getChannels();

        animationDurationInTicksPerSeconds = 0;

        for(AnimationModel.Channel channel : channels){

            AnimationModel.Sampler sampler = channel.getSampler();
            AccessorModel samplerInputAccessor = sampler.getInput();
            AccessorData samplerInput = samplerInputAccessor.getAccessorData();

            FloatBuffer times = samplerInput.createByteBuffer().asFloatBuffer();

            for(int i = 0; i < samplerInput.getNumElements(); i++){

                float time = times.get(i);

                animationDurationInTicksPerSeconds = Math.max(animationDurationInTicksPerSeconds, time);
            }
        }

        animationTicksPerSecond = 1;
    }

    @Override
    protected void loadFinalTransformation(double animationTime) {

        loadFinalNodesTransformations(rootNode, rootNodeParentNodeTransformation, animationTime);
    }

    private void loadFinalNodesTransformations(NodeModel node, Matrix4f parentTransformation, double animationTime){

        String nodeName = node.getName();

        Matrix4f nodeTransformation = getNodeTransformation(node, animationTime);
        Matrix4f globalTransformation = super.getGlobalTransformation(parentTransformation, nodeTransformation);

        super.loadFinalTransformation(nodeName, globalTransformation);

        List<NodeModel> childrenNodes = node.getChildren();

        for(NodeModel childNode : childrenNodes){

            loadFinalNodesTransformations(childNode, globalTransformation, animationTime);
        }
    }

    private Matrix4f getNodeTransformation(NodeModel node, double animationTime){

        String nodeName = node.getName();

        List<AnimationModel.Channel> foundNodeChannels = getChannelsWithName(nodeName);

        Matrix4f nodeTransformation = new Matrix4f().identity();

        if(!foundNodeChannels.isEmpty()){

            NodeChannels foundNodeOrderedChannels = getNodeOrderedChannels(foundNodeChannels);

            Vector3f translate = new Vector3f();
            float[] translateData = node.getTranslation();
            AnimationModel.Channel translateChannel = foundNodeOrderedChannels.translateChannel;
            if(translateChannel != null){

                translate = getInterpolatedVectorData(translateChannel, animationTime);
            }
            else if(translateData != null){
                translate = new Vector3f(node.getTranslation());
            }
            Matrix4f translation = new Matrix4f().translation(translate);

            Quaternionf rotate = new Quaternionf();
            float[] rotateData = node.getRotation();
            AnimationModel.Channel rotationChannel = foundNodeOrderedChannels.rotationChannel;
            if(rotationChannel != null){

                rotate = getRotationInterpolated(rotationChannel, animationTime);
            }
            else if(rotateData != null){
                rotate = new Quaternionf(rotateData[0], rotateData[1], rotateData[2], rotateData[3]);
            }
            Matrix4f rotation = new Matrix4f().rotation(rotate);

            Vector3f scale = new Vector3f(1, 1, 1);
            float[] scaleData = node.getScale();
            AnimationModel.Channel scaleChannel = foundNodeOrderedChannels.scaleChannel;
            if(scaleChannel != null){

                scale = getInterpolatedVectorData(scaleChannel, animationTime);
            }
            else if(scaleData != null){
                scale = new Vector3f(node.getScale());
            }
            Matrix4f scaling = new Matrix4f().scaling(scale);

            nodeTransformation = new Matrix4f(translation)
                .mul(rotation)
                .mul(scaling);
        }
        else{

            float[] nodeTransformationData = new float[16];
            node.computeLocalTransform(nodeTransformationData);
            nodeTransformation.set(nodeTransformationData);
        }

        return nodeTransformation;
    }

    private List<AnimationModel.Channel> getChannelsWithName(String name){

        List<AnimationModel.Channel> foundChannels = new ArrayList<>();

        List<AnimationModel.Channel> channels = animation.getChannels();

        for(AnimationModel.Channel channel : channels){

            NodeModel channelNode = channel.getNodeModel();
            String channelNodeName = channelNode.getName();

            if(Objects.equals(channelNodeName, name)){

                foundChannels.add(channel);
            }
        }

        return foundChannels;
    }

    private NodeChannels getNodeOrderedChannels(List<AnimationModel.Channel> channels){

        NodeChannels nodeChannels = new NodeChannels();

        for(AnimationModel.Channel channel : channels){

            String channelType = channel.getPath();

            switch(channelType){

                case "translation":
                    nodeChannels.translateChannel = channel;
                    break;

                case "rotation":
                    nodeChannels.rotationChannel = channel;
                    break;

                case "scale":
                    nodeChannels.scaleChannel = channel;
                    break;

                default:
                    nodeChannels.translateChannel = channel;
                    break;
            }
        }

        return nodeChannels;
    }

    private Vector3f getInterpolatedVectorData(AnimationModel.Channel channel, double actualTimeInTicks){

        AnimationModel.Sampler sampler = channel.getSampler();

        if(isOnlyOneInterpolationData(sampler)){

            ChannelVectorInterpolationData data = getInterpolatedVectorData(sampler, 0);

            return data.lessTimeVec;
        }

        ChannelTimeInterpolationData timeInterpolationData = getInterpolatedTimeData(sampler, actualTimeInTicks);
        int lessTimeIndex = timeInterpolationData.lessTimeIndex;

        ChannelVectorInterpolationData data = getInterpolatedVectorData(sampler, lessTimeIndex);

        double lessTime = timeInterpolationData.lessTime;
        Vector3f lessTimeVec = data.lessTimeVec;

        double moreTime = timeInterpolationData.moreTime;
        Vector3f moreTimeVec = data.moreTimeVec;

        return AnimatedGlbMesh.getInterpolated(lessTimeVec, lessTime, moreTimeVec, moreTime, actualTimeInTicks);
    }

    private boolean isOnlyOneInterpolationData(AnimationModel.Sampler sampler){

        AccessorModel timeDataAccessorModel = sampler.getInput();
        AccessorData timeDataAccessor = timeDataAccessorModel.getAccessorData();

        return timeDataAccessor.getNumElements() == 1;
    }

    private ChannelTimeInterpolationData getInterpolatedTimeData(AnimationModel.Sampler sampler, double actualTimeInTicks){

        AccessorModel timeDataAccessorModel = sampler.getInput();
        AccessorData timeDataAccessor = timeDataAccessorModel.getAccessorData();

        FloatBuffer timeData = timeDataAccessor.createByteBuffer().asFloatBuffer();

        int lessTimeIndex = 0;

        for (int i = 0; i < timeDataAccessor.getNumElements() - 1; i++){

            double laterDateTime = timeData.get(i + 1);

            if(laterDateTime > actualTimeInTicks){

                lessTimeIndex = i;
                break;
            }
        }

        return new ChannelTimeInterpolationData(
            lessTimeIndex,
            timeData.get(lessTimeIndex),
            timeData.get(lessTimeIndex + 1)
        );
    }

    private ChannelVectorInterpolationData getInterpolatedVectorData(AnimationModel.Sampler sampler, int lessTimeIndex){

        AccessorModel dataAccessorModel = sampler.getOutput();
        AccessorData dataAccessor = dataAccessorModel.getAccessorData();

        FloatBuffer data = dataAccessor.createByteBuffer().asFloatBuffer();

        int lessTimeFirstElementIndex = lessTimeIndex * 3;

        Vector3f lessTimeVec = new Vector3f(
            data.get(lessTimeFirstElementIndex),
            data.get(lessTimeFirstElementIndex + 1),
            data.get(lessTimeFirstElementIndex + 2)
        );

        if(dataAccessor.getNumElements() == 1){

            return new ChannelVectorInterpolationData(
                lessTimeVec,
                lessTimeVec
            );
        }

        int moreTimeFirstElementIndex = (lessTimeIndex + 1) * 3;

        Vector3f moreTimeVec = new Vector3f(
            data.get(moreTimeFirstElementIndex),
            data.get(moreTimeFirstElementIndex + 1),
            data.get(moreTimeFirstElementIndex + 2)
        );

        return new ChannelVectorInterpolationData(
            lessTimeVec,
            moreTimeVec
        );
    }

    private Quaternionf getRotationInterpolated(AnimationModel.Channel channel, double actualTimeInTicks){

        AnimationModel.Sampler sampler = channel.getSampler();

        if(isOnlyOneInterpolationData(sampler)){

            return getInterpolatedQuaternionData(sampler, 0).lessTimeQuaternion;
        }

        if (sampler.getInterpolation().name().equals("CUBICSPLINE")) {

            throw new UnsupportedOperationException("CUBICSPLINE not supported yet");
        }

        ChannelTimeInterpolationData timeInterpolationData = getInterpolatedTimeData(sampler, actualTimeInTicks);
        int lessTimeIndex = timeInterpolationData.lessTimeIndex;
        ChannelQuaternionInterpolationData interpolationData = getInterpolatedQuaternionData(sampler, lessTimeIndex);

        double lessTime = timeInterpolationData.lessTime;
        Quaternionf lessTimeQuaternion = interpolationData.lessTimeQuaternion;

        double moreTime = timeInterpolationData.moreTime;
        Quaternionf moreTimeQuaternion = interpolationData.moreTimeQuaternion;

        if (sampler.getInterpolation().name().equals("STEP")) {

            return new Quaternionf(lessTimeQuaternion);
        }

        return AnimatedGlbMesh.getInterpolated(lessTimeQuaternion, lessTime, moreTimeQuaternion, moreTime, actualTimeInTicks);
    }

    private ChannelQuaternionInterpolationData getInterpolatedQuaternionData(AnimationModel.Sampler sampler, int lessTimeIndex){

        AccessorModel dataAccessorModel = sampler.getOutput();
        AccessorData dataAccessor = dataAccessorModel.getAccessorData();

        FloatBuffer data = dataAccessor.createByteBuffer().asFloatBuffer();

        int lessTimeFirstElementIndex = lessTimeIndex * 4;

        Quaternionf lessTimeQuaternion = new Quaternionf(
            data.get(lessTimeFirstElementIndex),
            data.get(lessTimeFirstElementIndex + 1),
            data.get(lessTimeFirstElementIndex + 2),
            data.get(lessTimeFirstElementIndex + 3)
        );

        if(dataAccessor.getNumElements() == 1){

            return new ChannelQuaternionInterpolationData(
                lessTimeQuaternion,
                lessTimeQuaternion
            );
        }

        int moreTimeFirstElementIndex = (lessTimeIndex + 1) * 4;

        Quaternionf moreTimeQuaternion = new Quaternionf(
            data.get(moreTimeFirstElementIndex),
            data.get(moreTimeFirstElementIndex + 1),
            data.get(moreTimeFirstElementIndex + 2),
            data.get(moreTimeFirstElementIndex + 3)
        );

        return new ChannelQuaternionInterpolationData(
            lessTimeQuaternion,
            moreTimeQuaternion
        );
    }

    @Override
    public void draw() {

        super.draw();

//        drawDebugBones();
//        drawStaticBones();
    }

    public void drawDebugBones() {

        Map<String, Vector3f> bonePositions = getAnimatedBonesPositions();

        glLineWidth(6.0f);
        glBegin(GL_LINES);

        drawNodeLines(rootNode, bonePositions);

        glEnd();
    }

    private void drawNodeLines(NodeModel node, Map<String, Vector3f> positions) {

        String nodeName = node.getName();
        Vector3f parentPos = positions.get(nodeName);

        List<NodeModel> childrenNodes = node.getChildren();

        for (NodeModel childNode : childrenNodes) {

            String childName = childNode.getName();
            Vector3f childPos = positions.get(childName);

            if (parentPos != null && childPos != null) {

                glVertex3f(parentPos.x, parentPos.y, parentPos.z);
                glVertex3f(childPos.x, childPos.y, childPos.z);
            }

            drawNodeLines(childNode, positions);
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
        addStaticNodePositions(rootNode, positions);

        return positions;
    }

    private void addStaticNodePositions(NodeModel node, Map<String, Vector3f> positions) {

        String nodeName = node.getName();

        float[] globalTransformData = new float[16];
        node.computeGlobalTransform(globalTransformData);
        Matrix4f globalTransform = new Matrix4f().set(globalTransformData);

        Vector3f pos = new Vector3f();
        globalTransform.getTranslation(pos);

        positions.put(nodeName, pos);

        List<NodeModel> childrenNodes = node.getChildren();

        for (NodeModel childNode : childrenNodes) {

            addStaticNodePositions(childNode, positions);
        }
    }
}
