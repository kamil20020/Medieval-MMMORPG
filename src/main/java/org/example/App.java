package org.example;

import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.io.GltfModelReader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

        System.out.println("Hello World! :D");

        new Game().loop();

        GltfModel model = loadModel("animations/warrior-sword-fight.glb");

        for(NodeModel nm : model.getNodeModels()){
            System.out.println("Node name: " + nm.getName());

            // Jeżeli node ma mesh

            float[] mat = new float[16];
            nm.computeGlobalTransform(mat);

            System.out.println("Global Transform: " + Arrays.toString(mat));


            // 3. Skiny (kości)
            List<SkinModel> skins = model.getSkinModels();
            System.out.println("\nSkins:");
            for (SkinModel skin : skins) {
                System.out.println("Skin: " + skin.getName());

                // Kości (joints)
                List<NodeModel> joints = skin.getJoints();
                System.out.println("Joints:");
                for (NodeModel joint : joints) {
                    System.out.println(" - " + joint.getName());
                }

                // Macierze inverse bind pose
                AccessorModel invBindAccessor = skin.getInverseBindMatrices();
                if (invBindAccessor != null) {
                    System.out.println("Inverse bind matrices count: " + invBindAccessor.getCount());
                }
            }

            // Dzieci
            System.out.println("Children: " + nm.getChildren().size());

            List<AnimationModel> animations = model.getAnimationModels();
            System.out.println("\nAnimations:");
            for (AnimationModel anim : animations) {
                System.out.println("Animation: " + anim.getName());

                List<AnimationModel.Channel> channels = anim.getChannels();
                for (AnimationModel.Channel channel : channels) {
                    NodeModel targetNode = channel.getNodeModel();

                    NodeModel node = channel.getNodeModel();
                    String path = channel.getPath(); // "translation", "rotation", "scale"

                    AnimationModel.Sampler sampler = channel.getSampler();
                    AccessorModel inputAccessor = sampler.getInput();   // czasy keyframe
                    AccessorModel outputAccessor = sampler.getOutput(); // wartości keyframe

                    // Pobranie bajtów z buffera
                    ByteBuffer inputBuffer = inputAccessor.getBufferViewModel().getBufferViewData();
                    ByteBuffer outputBuffer = outputAccessor.getBufferViewModel().getBufferViewData();

                    // Konwersja do float (little endian)
                    inputBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    outputBuffer.order(ByteOrder.LITTLE_ENDIAN);

                    float[] times = new float[inputAccessor.getCount()];
                    for (int i = 0; i < times.length; i++) {
                        times[i] = inputBuffer.getFloat(i * Float.BYTES);
                    }

                    int componentCount = 3; // translation/scale -> 3, rotation -> 4
                    if (path.equals("rotation")) componentCount = 4;

                    float[][] values = new float[outputAccessor.getCount()][componentCount];
                    for (int i = 0; i < values.length; i++) {
                        for (int j = 0; j < componentCount; j++) {
                            values[i][j] = outputBuffer.getFloat((i * componentCount + j) * Float.BYTES);
                        }
                    }

                    System.out.println("Node: " + node.getName() + " Path: " + path);
                    System.out.println("Times: " + Arrays.toString(times));
                    System.out.println("Values: " + Arrays.deepToString(values));
                }

//                List<AnimationSampler> samplers = anim.getSamplers();
//                System.out.println("Samplers count: " + samplers.size());
            }

        }
    }

    public static GltfModel loadModel(String filePath) {

        File file = new File(filePath);

        GltfModelReader reader = new GltfModelReader();
        GltfModel model = null;

        try {

            model = reader.read(file.toPath());
        }
        catch (IOException e) {

            e.printStackTrace();
        }

        return model;
    }
}
