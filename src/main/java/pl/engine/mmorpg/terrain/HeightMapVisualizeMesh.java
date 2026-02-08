package pl.engine.mmorpg.terrain;

import org.joml.Vector4f;
import pl.engine.mmorpg.mesh.VisualizeMesh;

import java.util.Map;

public class HeightMapVisualizeMesh extends VisualizeMesh {

    private static final int xNumberOfPoints = 500;
    private static final int zNumberOfPoints = 500;
    private static final Vector4f color = new Vector4f(0, 1, 0, 1);

    public HeightMapVisualizeMesh(float minX, float minZ, Map<String, Float> heightMap){

        super(loadVertices(minX, minZ, heightMap), color);
    }

    private static float[] loadVertices(float minX, float minZ, Map<String, Float> heightMap){

        float[] vertices = new float[xNumberOfPoints * zNumberOfPoints * 3];

        for(int xI = 0, vertexIndex = 0; xI < xNumberOfPoints; xI++){

            for(int zI = 0; zI < zNumberOfPoints; zI++){

                vertices[vertexIndex] =  minX + xI;

                String key = ((int)(minX + xI)) + " " + ((int) minZ + zI);

                if(!heightMap.containsKey(key)){
                    continue;
                }

                vertices[vertexIndex + 1] = heightMap.get(key);
                vertices[vertexIndex + 2] = minZ + zI;

                vertexIndex += 3;
            }
        }

        return vertices;
    }
}
