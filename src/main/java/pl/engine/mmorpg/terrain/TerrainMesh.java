package pl.engine.mmorpg.terrain;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.Map;

public class TerrainMesh implements Meshable {

    private final float[] vertices;
    private final int[] faces;
    private final ComplexMesh mesh;
    private Map<String, Float> heightMap;
    private Vector3f minCoords;
    private Vector3f maxCoords;

    private static final Logger logger = LoggerFactory.getLogger(TerrainMeshHeightMapGenerator.class);

    public TerrainMesh(String terrainFilePath, MeshAbstractFactory meshFactory){

        this.mesh = meshFactory.createComplexMesh(terrainFilePath);
        this.vertices = mesh.getVertices();
        this.faces = mesh.getFaces();

        logger.info("Loaded terrain");
        logger.info("Number of vertices {}", mesh.getNumberOfVertices());
        logger.info("Number of faces {}", mesh.getNumberOfFaces());

///*        for(int i = 0; i < vertices.length; i += 3){
//
//            vertices[i] *= 0.02;
//            vertices[i + 1] *= 0.02;
//            vertices[i + 2] *= 0.02;
//
//            vertices[i] = -vertices[i];
//        }*/
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();
    }

    @Override
    public void setModel(Matrix4f model) {

        mesh.setModel(model);
    }

    @Override
    public void draw() {

        mesh.draw();
    }

    @Override
    public void clear() {

        mesh.clear();
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        mesh.update(deltaTimeInSeconds);
    }

    @Override
    public int getNumberOfVertices() {

        return mesh.getNumberOfVertices();
    }

    @Override
    public int getNumberOfFaces() {

        return mesh.getNumberOfFaces();
    }

    @Override
    public float[] getVertices() {

        return mesh.getVertices();
    }

    @Override
    public int[] getFaces() {

        return mesh.getFaces();
    }

    public TerrainMeshHeightMapData generateHeightMap() {

        TerrainMeshHeightMapData terrainMeshHeightMapData = TerrainMeshHeightMapGenerator.generate(vertices, faces);

        minCoords = terrainMeshHeightMapData.minCoords();
        maxCoords = terrainMeshHeightMapData.maxCoords();
        heightMap = terrainMeshHeightMapData.heightMap();

        return terrainMeshHeightMapData;
    }

    public Map<String, Float> getHeightMap(){

        return heightMap;
    }

    public Vector3f getMinCoords(){

        return minCoords;
    }

    public Vector3f getMaxCoords(){

        return maxCoords;
    }
}
