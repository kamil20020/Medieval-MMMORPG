package pl.engine.mmorpg.entity;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.gravity.GravityComponent;
import pl.engine.mmorpg.terrain.TerrainMesh;

public class TerrainCollisionComponent {

    private static TerrainCollisionComponent INSTANCE = null;

    private final TerrainMesh terrainMesh;

    public static final double GRAVITY_SPEED = -25;
    public static final double GRAVITY_SPEED_POSITIVE = -GRAVITY_SPEED;

    private TerrainCollisionComponent(TerrainMesh terrainMesh){

        this.terrainMesh = terrainMesh;
    }

    public static TerrainCollisionComponent getInstance(TerrainMesh terrainMesh){

        if(INSTANCE != null){
            return INSTANCE;
        }

        INSTANCE = new TerrainCollisionComponent(terrainMesh);

        return INSTANCE;
    }

    public static TerrainCollisionComponent getInstance(){

        return INSTANCE;
    }

    public boolean isInAir(Vector3f position){

        if(terrainMesh.isOutside(position.x, position.z)){

            return false;
        }

        double maxValidY = terrainMesh.getTerrainMaxY(position.x, position.z);

        return position.y > maxValidY;
    }

    public double getCollisionMove(Vector3f position){

        if(terrainMesh.isOutside(position.x, position.z)){

            return 0;
        }

        double maxValidY = terrainMesh.getTerrainMaxY(position.x, position.z);

        if(position.y < maxValidY){

            return maxValidY - position.y;
        }

        return 0;
    }
}
