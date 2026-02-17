package pl.engine.mmorpg.entity;

import org.joml.Vector3f;
import pl.engine.mmorpg.terrain.TerrainMesh;
import pl.engine.mmorpg.terrain.TerrainMeshHeightMapData;

import java.util.Map;

public class GravityComponent {

    private static GravityComponent INSTANCE = null;

    private final TerrainMeshHeightMapData terrainHeightMapData;

    public static final double gravitySpeed = -0.1;

    private GravityComponent(TerrainMeshHeightMapData terrainHeightMapData){

        this.terrainHeightMapData = terrainHeightMapData;
    }

    public static GravityComponent getInstance(TerrainMeshHeightMapData terrainHeightMapData){

        if(INSTANCE != null){
            return INSTANCE;
        }

        INSTANCE = new GravityComponent(terrainHeightMapData);

        return INSTANCE;
    }

    public static GravityComponent getInstance(){

        return INSTANCE;
    }

    public double getYMove(Vector3f position){

        double x = position.x;
        double z = position.z;

        Vector3f minCoords = terrainHeightMapData.minCoords();
        Vector3f maxCoords = terrainHeightMapData.maxCoords();

        if(x < minCoords.x || x > maxCoords.x){
            return gravitySpeed;
        }

        if(z < minCoords.z || z > maxCoords.z){
            return gravitySpeed;
        }

        double leftX = Math.floor(x);
        double leftZ = Math.floor(z);

        double terrainMaxY = getTerrainMaxY(x, z, leftX, leftZ);

        if(position.y <= terrainMaxY){

            return 0;
        }

        return gravitySpeed;
    }

    private double getTerrainMaxY(double x, double z, double leftX, double leftZ){

        if(leftX == x && leftZ == z){

            try{

                return terrainHeightMapData.getValue(leftX, leftZ);
            }
            catch(NullPointerException e){
                System.out.println("AA");
            }
        }

        if(leftX == x){

            return interpolateYInZ(x, z, leftZ);
        }

        if(leftZ == z){

            return interpolateYInX(z, x, leftX);
        }

        double xZ1Y = interpolateYInX(leftZ, x, leftX);
        double xZ2Y = interpolateYInX(leftZ + 1, x, leftX);

        return interpolate1D(z, leftZ, xZ1Y, xZ2Y);
    }

    private double interpolateYInZ(double x, double z, double leftZ) {

        double leftZY = terrainHeightMapData.getValue(x, leftZ);
        double rightZY = terrainHeightMapData.getValue(x, leftZ + 1);

        return interpolate1D(z, leftZ, leftZY, rightZY);
    }

    private double interpolateYInX(double z, double x, double leftX) {

        double leftXY = terrainHeightMapData.getValue(leftX, z);
        double rightXY = terrainHeightMapData.getValue(leftX + 1, z);

        return interpolate1D(x, leftX, leftXY, rightXY);
    }

    private double interpolate1D(double middleCoords, double leftCoords, double leftValue, double rightValue){

        if(middleCoords == leftCoords){
            return leftValue;
        }

        if(middleCoords == leftCoords + 1){
            return rightValue;
        }

        double coordsDiff = middleCoords - leftCoords;
        double valueDiff = rightValue - leftValue + 1;

        return leftValue + coordsDiff * valueDiff;
    }
}
