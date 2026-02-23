package pl.engine.mmorpg.terrain;

import org.joml.Vector4f;
import pl.engine.mmorpg.mesh.VisualizeMesh;

public class DenseHeightMapVisualizeMesh extends VisualizeMesh {

    private static final int xNumberOfPoints = 500;
    private static final int zNumberOfPoints = 500;
    private static final Vector4f color = new Vector4f(0, 0, 1, 1);
    private static final float COORDS_STEP = 0.5f;

    public DenseHeightMapVisualizeMesh(TerrainMesh terrainMesh){

        super(loadVertices(terrainMesh.getMinCoords().x, terrainMesh.getMinCoords().z, terrainMesh), color); // in i max coords to -250 do 250
    }

    private static float[] loadVertices(float minX, float minZ, TerrainMesh terrainMesh){

        int coordsStepMultiplier = (int) (1.0 / COORDS_STEP);
        float[] vertices = new float[xNumberOfPoints * zNumberOfPoints * 3 * coordsStepMultiplier * coordsStepMultiplier];
        int vertexIndex = 0;

        for(float x = 0; x < xNumberOfPoints; x += COORDS_STEP){

            for(float z = 0; z < zNumberOfPoints; z += COORDS_STEP, vertexIndex += 3){

                if(z < 0){
                    z = (float) (Math.ceil(z * 100d) / 100d);
                }
                else{
                    z = (float) (Math.floor(z * 100d) / 100d);
                }

                vertices[vertexIndex] = minX + x;
                vertices[vertexIndex + 1] = (float) terrainMesh.getTerrainMaxY(minX + x, minZ + z);
                vertices[vertexIndex + 2] = minZ + z;
            }
        }

        return vertices;
    }
}
