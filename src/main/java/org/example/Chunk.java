package org.example;

import org.example.mesh.*;
import texture.FileTexture;
import texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private List<Meshable> meshables = new ArrayList<>();
    private int modelId;
    private final Camera camera;
    private final EventsHandler eventsHandler;

    public Chunk(int modelId, Camera camera, EventsHandler eventsHandler){

        this.modelId = modelId;
        this.camera = camera;
        this.eventsHandler = eventsHandler;
    }

    public void init(){

        uploadToGpu();
    }

    private void uploadToGpu(){

        Texture texture = new FileTexture("textures/grass.png", Rect.TEXTURE_COORDS);
        Meshable grass = new Rect(texture);
        meshables.add(grass);

        Meshable player = new Player(camera, modelId, eventsHandler);
        meshables.add(player);

        for(Meshable meshable : meshables){

            meshable.uploadToGpu();
        }
    }

    public void draw(){

        for(Meshable meshable : meshables){

            meshable.draw();
        }
    }

    public void clear(){

        for(Meshable meshable : meshables){

            meshable.clear();
        }
    }
}
