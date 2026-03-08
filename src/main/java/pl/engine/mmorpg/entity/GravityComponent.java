package pl.engine.mmorpg.entity;

import org.joml.Vector3f;
import pl.engine.mmorpg.terrain.TerrainMesh;

public class GravityComponent {

    private static GravityComponent INSTANCE = null;

    private final TerrainMesh terrainMesh;

    public static final double GRAVITY_SPEED = -0.3;
    public static final double GRAVITY_SPEED_POSITIVE = -GRAVITY_SPEED;

    private GravityComponent(TerrainMesh terrainMesh){

        this.terrainMesh = terrainMesh;
    }

    public static GravityComponent getInstance(TerrainMesh terrainMesh){

        if(INSTANCE != null){
            return INSTANCE;
        }

        INSTANCE = new GravityComponent(terrainMesh);

        return INSTANCE;
    }

    public static GravityComponent getInstance(){

        return INSTANCE;
    }

    public double getNewYMove(Vector3f position){

        double x = position.x;
        double z = position.z;

        Vector3f minCoords = terrainMesh.getMinCoords();
        Vector3f maxCoords = terrainMesh.getMaxCoords();

        if(x < minCoords.x || x > maxCoords.x){
            return 0;
        }

        if(z < minCoords.z || z > maxCoords.z){
            return 0;
        }

        double terrainMaxY = terrainMesh.getTerrainMaxY(x, z);

        if(position.y <= terrainMaxY){

            return terrainMaxY - position.y;
        }

        return GRAVITY_SPEED;
    }

    public TerrainMesh getTerrainMesh(){

        return terrainMesh;
    }
}
