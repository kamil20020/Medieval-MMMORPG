package org.example;

import org.example.mesh.*;
import texture.FileTexture;
import texture.Texture;

public class Chunk {

    private Meshable meshable;

    public void init(){

        uploadToGpu();
    }

    private void uploadToGpu(){

        meshable = new ComplexGlbMesh("models/warrior.glb");
//        Texture texture = new FileTexture("textures/ground-test.png", Rect.TEXTURE_COORDS);
//        meshable = new Rect(texture);
        meshable.uploadToGpu();


    }

    public void draw(){

        meshable.draw();
    }

    public void clear(){

        meshable.clear();
    }
}
