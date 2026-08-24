package pl.engine.mmorpg.render;

import org.joml.Vector4f;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.animation.DynamicMesh;
import pl.engine.mmorpg.entity.gravity.TerrainCollisionComponent;
import pl.engine.mmorpg.entity.player.Player;
import pl.engine.mmorpg.mesh.*;
import pl.engine.mmorpg.terrain.*;
import pl.engine.mmorpg.texture.FileTexture;
import pl.engine.mmorpg.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    protected List<Meshable> meshables = new ArrayList<>();
    protected final EventsHandler eventsHandler;
    protected final Window window;
    private final MeshAbstractFactory meshFactory;

    public Chunk(Window window, EventsHandler eventsHandler, MeshAbstractFactory meshFactory){

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
//        meshables.add(grass);

        TerrainMesh terrain = TerrainMesh.getInstance("models/snow1.glb", meshFactory);
        terrain.generateHeightMap();
        meshables.add(terrain);

        Meshable player = new Player(eventsHandler, meshFactory); //new Player(camera, eventsHandler, meshFactory);
        meshables.add(player);

//        Meshable playerCopy = meshFactory.createComplexMesh("models/entities/warrior.glb");
//        DynamicMesh dynamicMesh = new DynamicMesh(playerCopy, 1);
//        meshables.add(dynamicMesh);

//        Meshable model = new ComplexJgltfMesh("animations/dragon1.glb"); //new Player(camera, eventsHandler, meshFactory);
//        meshables.add(model);

//        Meshable terrain = new ComplexJgltfMesh("models/ruines.glb"); //new Player(camera, eventsHandler, meshFactory);
//        terrain.setModel(new Matrix4f().identity().rotateX(-90));
//        meshables.add(terrain);
//        ComplexMesh s = meshFactory.createComplexMesh("models/s.glb");
//        meshables.add(s);
//
//        Texture texture1 = new FileTexture("textures/wood.png", Cube.TEXTURE_COORDS);
//        Meshable wood = new Cube(texture1);
//        meshables.add(wood);

//         terrain.generateHeightMap();
//        TerrainCollisionComponent.getInstance(terrain);
        //new Player(camera, eventsHandler, meshFactory);
//        terrain.setModel(new Matrix4f().identity().scaling(0.01f).rotateX((float) Math.toRadians(-90)));
//        terrain.setModel(new Matrix4f().identity().rotateX((float) Math.toRadians(-90)));
//        terrain.setModel(new Matrix4f().identity().scaling(0.02f).rotateX((float) Math.toRadians(180)));
//        terrain.setModel(new Matrix4f().identity().scaling(50f));
//       HeightMapVisualizeMesh heightMapVisualizeMesh = new HeightMapVisualizeMesh(-250, -250, heightMapData.heightMap());
//        heightMapVisualizeMesh.setModel(new Matrix4f().identity().scaling(0.01f).rotateX((float) Math.toRadians(-90)));
//        DenseHeightMapVisualizeMesh denseHeightMapVisualizeMesh = new DenseHeightMapVisualizeMesh(terrain);
//        VisualizeMesh visualizeMesh = new VisualizeMesh(terrain.getVertices(), new Vector4f(0, 0, 1, 1));
//        meshables.add(visualizeMesh);
//        meshables.add(terrain);
//        meshables.add(denseHeightMapVisualizeMesh);
//        meshables.add(heightMapVisualizeMesh);
//        TerrainMeshHeightMapGenerator.saveToCsv(heightMap, "");

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
