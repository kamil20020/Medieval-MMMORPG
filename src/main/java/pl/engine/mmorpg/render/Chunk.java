package pl.engine.mmorpg.render;

import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.entity.Player;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.mesh.Rect;
import pl.engine.mmorpg.mesh.libraries.jgltf.ComplexJgltfMesh;
import pl.engine.mmorpg.texture.FileTexture;
import pl.engine.mmorpg.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    protected List<Meshable> meshables = new ArrayList<>();
    protected final Camera camera;
    protected final EventsHandler eventsHandler;
    protected final Window window;
    private final MeshAbstractFactory meshFactory;

    public Chunk(Camera camera, Window window, EventsHandler eventsHandler, MeshAbstractFactory meshFactory){

        this.camera = camera;
        this.eventsHandler = eventsHandler;
        this.window = window;
        this.meshFactory = meshFactory;
    }

    public void init(){

        uploadToGpu();
    }

    private void uploadToGpu(){

        Texture texture = new FileTexture("textures/grass.png", Rect.TEXTURE_COORDS);
        Meshable grass = new Rect(texture);
        meshables.add(grass);

        Meshable player = new Player(camera, eventsHandler, meshFactory); //new Player(camera, eventsHandler, meshFactory);
        meshables.add(player);

        Meshable model = new ComplexJgltfMesh("animations/dragon1.glb"); //new Player(camera, eventsHandler, meshFactory);
        meshables.add(model);

//        Meshable terrain = new ComplexJgltfMesh("models/terrain.glb"); //new Player(camera, eventsHandler, meshFactory);
//        meshables.add(terrain);

        for(Meshable meshable : meshables){

            meshable.uploadToGpu();
        }
    }

    public void update(double deltaTime){

        for(Meshable meshable : meshables){

            meshable.update(deltaTime);
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
