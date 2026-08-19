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

    private static TerrainMesh INSTANCE = null;

    private final float[] vertices;
    private final int[] faces;
    private final ComplexMesh mesh;
    private TerrainMeshHeightMapData terrainMeshHeightMapData;

    private static final Logger logger = LoggerFactory.getLogger(TerrainMeshHeightMapGenerator.class);

    private TerrainMesh(String terrainFilePath, MeshAbstractFactory meshFactory){

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

    public static TerrainMesh getInstance(String terrainFilePath, MeshAbstractFactory meshFactory){

        if(INSTANCE == null){
            INSTANCE = new TerrainMesh(terrainFilePath, meshFactory);
        }

        return INSTANCE;
    }

    public static TerrainMesh getInstance(){

        return INSTANCE;
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

    public void generateHeightMap() {

        terrainMeshHeightMapData = TerrainMeshHeightMapGenerator.generate(vertices, faces);
    }

    public double getTerrainMaxY(double x, double z){

        return getTerrainMaxY(x, z, Math.floor(x), Math.floor(z));
    }

    private double getTerrainMaxY(double x, double z, double leftX, double leftZ){

        if(leftX == x && leftZ == z){

            return terrainMeshHeightMapData.getValue(leftX, leftZ);
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

    private double interpolateYInX(double z, double x, double leftX) {

        double leftXY = terrainMeshHeightMapData.getValue(leftX, z);
        double rightXY = terrainMeshHeightMapData.getValue(leftX + 1, z);

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
        double valueDiff = rightValue - leftValue;

        return leftValue + coordsDiff * valueDiff;
    }

    private double interpolateYInZ(double x, double z, double leftZ) {

        double leftZY = terrainMeshHeightMapData.getValue(x, leftZ);
        double rightZY = terrainMeshHeightMapData.getValue(x, leftZ + 1);

        return interpolate1D(z, leftZ, leftZY, rightZY);
    }

    public Map<String, Float> getHeightMap(){

        return terrainMeshHeightMapData.heightMap();
    }

    public Vector3f getMinCoords(){

        return terrainMeshHeightMapData.minCoords();
    }

    public Vector3f getMaxCoords(){

        return terrainMeshHeightMapData.maxCoords();
    }

    public double terrainPointVerticalDifference(Vector3f position) {

        if(!isInsideTerrainHorizontal(position.x, position.z)){
            return 0d;
        }

        double maxValidY = getTerrainMaxY(position.x, position.z);

        return maxValidY - position.y;
    }

    public boolean isInsideTerrainHorizontal(double x, double z){

        return x >= getMinCoords().x && x <= getMaxCoords().x &&
               z >= getMinCoords().z && z <= getMaxCoords().z;
    }
}
