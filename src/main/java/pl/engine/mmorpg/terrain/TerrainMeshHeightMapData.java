package pl.engine.mmorpg.terrain;

import org.joml.Vector3f;

import java.util.Map;

public record TerrainMeshHeightMapData(

    Vector3f minCoords,
    Vector3f maxCoords,
    Map<String, Float> heightMap
){
    public double getValue(double x, double z){

        String key = getHeightMapKey(x, z);

        try {
            return heightMap.get(key);
        }
        catch (NullPointerException e){
           int a = 2;
        }
        return 0;
    }

    public void addValue(double x, double z, double y){

        String key = getHeightMapKey(x, z);

        heightMap.put(key, (float) y);
    }

    public static String getHeightMapKey(double x, double z){

        int convertedX = (int) x;
        int convertedZ = (int) z;

        return convertedX + " " + convertedZ;
    }
}
