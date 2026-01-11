package pl.engine.mmorpg;

import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.mesh.Rect;
import pl.engine.mmorpg.texture.FileTexture;
import pl.engine.mmorpg.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    protected List<Meshable> meshables = new ArrayList<>();
    protected final Camera camera;
    protected final EventsHandler eventsHandler;
    protected final Window window;

    public Chunk(Camera camera, Window window, EventsHandler eventsHandler){

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

//        Meshable newModel = new AnimatedComplexJgltfGlbMesh(
//            "animations/dragon1.glb",
//            "animations/dragon1.glb"
//        );
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
