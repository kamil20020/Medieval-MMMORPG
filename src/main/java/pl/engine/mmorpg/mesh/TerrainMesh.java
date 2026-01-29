package pl.engine.mmorpg.mesh;

import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerrainMesh implements Meshable{

    private final float[] vertices;
    private final int[] faces;
    private final ComplexMesh mesh;

    private static final Logger logger = LoggerFactory.getLogger(TerrainMeshHeightMapGenerator.class);

    public TerrainMesh(String terrainFilePath, MeshAbstractFactory meshFactory){

        this.mesh = meshFactory.createComplexMesh(terrainFilePath);
        this.vertices = mesh.getVertices();
        this.faces = mesh.getFaces();

        logger.info("Loaded terrain");
        logger.info("Number of vertices {}", mesh.getNumberOfVertices());
        logger.info("Number of faces {}", mesh.getNumberOfFaces());
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

    public float[][] generateHeightMap() {

        return TerrainMeshHeightMapGenerator.generate(vertices, faces);
    }
}
