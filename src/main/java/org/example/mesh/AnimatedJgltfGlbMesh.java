package org.example.mesh;

import de.javagl.jgltf.model.*;
import org.joml.Matrix4f;
import texture.JgltfTexture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class AnimatedJgltfGlbMesh extends AnimatedGlbMesh{

    private final MeshModel mesh;
    private final NodeModel meshNode;
    private final GltfModel globalModel;
    private NodeModel rootNode;

    public AnimatedJgltfGlbMesh(MeshModel mesh, JgltfTexture texture) {
        super(new JgltfGlbMesh(mesh, texture));

        this.mesh = mesh;

        this.globalModel = ComplexJgltfGlbMesh.load("animations/warrior-sword-fight.glb");
        this.meshNode = findMeshNodeModel(globalModel, mesh.getName());

        loadBones();
//        sortVerticesBones();
        normalizeVerticesWeightsAndIndices();
        initAnimation();

        System.out.println(numberOfBones);
    }

    private NodeModel findMeshNodeModel(GltfModel model, String meshName) throws IllegalStateException{

        for(var node : model.getNodeModels()){

            String nodeName = node.getName();

            if(Objects.equals(nodeName, meshName)){

                return node;
            }
        }

        throw new IllegalStateException("Nie znaleziono węzła dla mesha " + mesh.getName());
    }

    @Override
    protected void loadBones() {

        loadBonesNames();
        this.numberOfBones = bonesNamesIndices.size();
        loadVerticesBonesIndicesAndWeights();
        loadBonesInverses();
    }

    private void loadBonesNames(){

        SkinModel skinModel = meshNode.getSkinModel();

        List<NodeModel> bones = skinModel.getJoints();

        for(int boneIndex = 0; boneIndex < bones.size(); boneIndex++){

            NodeModel bone = bones.get(boneIndex);

            String boneName = bone.getName();

            bonesNamesIndices.put(boneName, boneIndex);
        }
    }

    private void loadVerticesBonesIndicesAndWeights(){

        MeshPrimitiveModel meshPrimitiveModel = mesh.getMeshPrimitiveModels().get(0);

        AccessorData bonesIndicesDataAccessor = meshPrimitiveModel.getAttributes().get("JOINTS_0").getAccessorData();
        ByteBuffer bonesIndicesData = bonesIndicesDataAccessor.createByteBuffer();

        AccessorData bonesWeightsDataAccessor = meshPrimitiveModel.getAttributes().get("WEIGHTS_0").getAccessorData();
        FloatBuffer bonesWeightsData = bonesWeightsDataAccessor.createByteBuffer().asFloatBuffer();

        for(int vertexIndex = 0; vertexIndex < numberOfVertices; vertexIndex++){

            for(int j = 0; j < MAX_NUMBER_OF_BONS_PER_VERTEX; j++){

                int boneIndex = bonesIndicesData.get();
                float boneWeight = bonesWeightsData.get();

                verticesBonesIndices.get(vertexIndex).add(boneIndex);
                verticesBonesWeights.get(vertexIndex).add(boneWeight);
            }
        }

        bonesIndicesData.flip();
        bonesWeightsData.flip();
    }

    private void loadBonesInverses(){

        SkinModel skinModel = meshNode.getSkinModel();

        AccessorModel dataAccessorModel = skinModel.getInverseBindMatrices();
        AccessorData dataAccessor = dataAccessorModel.getAccessorData();
        FloatBuffer data = dataAccessor.createByteBuffer().asFloatBuffer();;

        for(int boneIndex = 0; boneIndex < numberOfBones; boneIndex++){

            float[] boneInverseData = new float[16];
            data.get(boneIndex * 16, boneInverseData);

            Matrix4f convertedBoneInverseData = new Matrix4f().set(boneInverseData);

            bonesInverses.add(convertedBoneInverseData);
        }
    }

    @Override
    protected void loadAnimationData() {

        AnimationModel animationModel = globalModel.getAnimationModels().get(0);
        SceneModel firstSceneModel = globalModel.getSceneModels().get(0);
        rootNode = firstSceneModel.getNodeModels().get(0);

        System.out.println(firstSceneModel.getNodeModels().get(0).getName());
    }

    @Override
    protected void loadFinalTransformation(double deltaTimeInSeconds) {

    }

//    private Map<String, Vector3f> getStaticBonePositions() {
//
//        Map<String, Vector3f> positions = new LinkedHashMap<>();
//        addStaticNodePositions(rootNode, new Matrix4f().identity(), positions);
//        return positions;
//    }
//
//    private void addStaticNodePositions(AINode node, Matrix4f parentTransform, Map<String, Vector3f> positions) {
//        String nodeName = node.mName().dataString();
//        Matrix4f nodeTransform = convert(node.mTransformation());
//        Matrix4f globalTransform = new Matrix4f(parentTransform).mul(nodeTransform);
//
//        Vector3f pos = new Vector3f();
//        globalTransform.getTranslation(pos);
//
//        positions.put(nodeName, pos);
//
//        PointerBuffer children = node.mChildren();
//        for (int i = 0; i < node.mNumChildren(); i++) {
//            long childId = children.get(i);
//            AINode child = AINode.create(childId);
//            addStaticNodePositions(child, globalTransform, positions);
//        }
//    }
//
//    public void drawStaticBones() {
//        Map<String, Vector3f> bonePositions = getStaticBonePositions();
//
//        glLineWidth(5.0f);
//        glBegin(GL_LINES);
//
//        drawStaticNodeLines(rootNode, bonePositions);
//
//        glEnd();
//    }
//
//    private void drawStaticNodeLines(AINode node, Map<String, Vector3f> positions) {
//        String nodeName = node.mName().dataString();
//        Vector3f parentPos = positions.get(nodeName);
//
//        PointerBuffer children = node.mChildren();
//        for (int i = 0; i < node.mNumChildren(); i++) {
//            long childId = children.get(i);
//            AINode child = AINode.create(childId);
//
//            Vector3f childPos = positions.get(child.mName().dataString());
//            if (parentPos != null && childPos != null) {
//                glVertex3f(parentPos.x, parentPos.y, parentPos.z);
//                glVertex3f(childPos.x, childPos.y, childPos.z);
//            }
//
//            drawStaticNodeLines(child, positions);
//        }
//    }
}
