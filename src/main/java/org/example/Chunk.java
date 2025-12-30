package org.example;

import org.example.mesh.libraries.assimp.animation.AnimatedComplexAssimpGlbModel;
import org.example.mesh.libraries.jgltf.animation.AnimatedComplexJgltfGlbMesh;
import org.example.mesh.*;
import texture.FileTexture;
import texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    protected List<Meshable> meshables = new ArrayList<>();
    protected int modelId;
    public static int finalBoneMatricesId;
    protected final Camera camera;
    protected final EventsHandler eventsHandler;
    protected final Window window;

    public Chunk(int modelId, int finalBoneMatricesId, Camera camera, Window window, EventsHandler eventsHandler){

        this.modelId = modelId;
        Chunk.finalBoneMatricesId = finalBoneMatricesId;
        this.camera = camera;
        this.eventsHandler = eventsHandler;
        this.window = window;
    }

    public void init(){

        uploadToGpu();
    }

    private void uploadToGpu(){

        Texture texture = new FileTexture("textures/grass.png", Rect.TEXTURE_COORDS);
        Meshable grass = new Rect(texture);
        meshables.add(grass);

        Meshable player = new Player(camera, eventsHandler);
        meshables.add(player);
//
//        Meshable newModel = new AnimatedComplexAssimpGlbModel("animations/warrior-sword-fight.glb", "animations/warrior-sword-fight.glb");
//        meshables.add(newModel);
//
//        Meshable newModel1 = new AnimatedComplexAssimpGlbModel("animations/warrior-sword-fight.glb", "animations/warrior-sword-fight.glb");
//        meshables.add(newModel1);

        for(Meshable meshable : meshables){

            meshable.uploadToGpu();
        }
    }

    public void draw(double deltaTime){

        for(Meshable meshable : meshables){

            meshable.update(deltaTime);
            meshable.draw();
        }
    }
    public void clear(){

        for(Meshable meshable : meshables){

            meshable.clear();
        }
    }
}
