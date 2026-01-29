package pl.engine.mmorpg.mesh;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TerrainMeshHeightMapGenerator {

    private final float[] vertices;
    private final int[] faces;

    private final int numberOfXPoints;
    private final int numberOfZPoints;

    private final int numberOfXBlocks;
    private final int numberOfZBlocks;

    private final float[][] trianglesAABB;
    private final List<Integer>[][] blocksTrianglesMappings;
    private final float[][] result;

    private static final int BLOCK_SIZE = 100;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private static final Logger logger = LoggerFactory.getLogger(TerrainMeshHeightMapGenerator.class);
    
    private TerrainMeshHeightMapGenerator(float[] vertices, int[] faces){

        this.vertices = vertices;
        this.faces = faces;

        logger.info("");
        logger.info("Started generating height map for terrain");

        Vector3f[] minAndMaxCoords = getMinAndMaxCoords();
        Vector3f minCoords = minAndMaxCoords[0];
        Vector3f maxCoords = minAndMaxCoords[1];

        numberOfXPoints = (int) (maxCoords.x - minCoords.x);
        numberOfZPoints = (int) (maxCoords.z - minCoords.z);

        numberOfXBlocks = (int) Math.ceil((maxCoords.x - minCoords.x) / BLOCK_SIZE);
        numberOfZBlocks = (int) Math.ceil((maxCoords.y - minCoords.y) / BLOCK_SIZE);

        trianglesAABB = getTrianglesMinAndMaxCoords();
        blocksTrianglesMappings = getBlocksTrianglesMappings();
        result = new float[numberOfXPoints][numberOfZPoints];

        logger.info("");
        logger.info("Initialized terrain height generator params");
        logger.info("Min and max mesh coords {}, {}", minCoords, maxCoords);
        logger.info("Number of points (x, y) ({}, {})", numberOfXPoints, numberOfZPoints);
        logger.info("Number of blocks (x, y) ({}, {})", numberOfXBlocks, numberOfZBlocks);
        logger.info("Triangles AABB size ({}, {})", trianglesAABB.length, trianglesAABB[0].length);
        logger.info("Blocks triangles mappings size ({}, {})", blocksTrianglesMappings.length, blocksTrianglesMappings[0].length);
        logger.info("Result size ({}, {})", result.length, result[0].length);
    }

    private Vector3f[] getMinAndMaxCoords(){

        Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

        for(int i = 0; i < vertices.length; i += 3){

            float x = vertices[i];
            float y = vertices[i + 1];
            float z = vertices[i + 2];

            min.x = Math.min(x, min.x);
            min.y = Math.min(y, min.y);
            min.z = Math.min(z, min.z);

            max.x = Math.max(x, max.x);
            max.y = Math.max(y, max.y);
            max.z = Math.max(z, max.z);
        }

        logger.info("Generated min and max mesh coords");

        return new Vector3f[]{min, max};
    }

    private float[][] getTrianglesMinAndMaxCoords(){

        Vector3f triangleA = new Vector3f();
        Vector3f triangleB = new Vector3f();
        Vector3f triangleC = new Vector3f();

        float[][] result = new float[faces.length / 3][4];

        for(int facesElementIndex = 0, resultIndex = 0; facesElementIndex < faces.length; facesElementIndex += 3, resultIndex++) {

            loadTriangleVertex(facesElementIndex, triangleA);
            loadTriangleVertex(facesElementIndex + 1, triangleB);
            loadTriangleVertex(facesElementIndex + 2, triangleC);

            float[] row = new float[4];

            row[0] = Math.min(triangleA.x, Math.min(triangleB.x, triangleC.x));
            row[1] = Math.max(triangleA.x, Math.max(triangleB.x, triangleC.x));

            row[2] = Math.min(triangleA.z, Math.min(triangleB.z, triangleC.z));
            row[3] = Math.max(triangleA.z, Math.max(triangleB.z, triangleC.z));

            result[resultIndex] = row;
        }

        logger.info("Generated min and max triangles coords");

        return result;
    }

    private List<Integer>[][] getBlocksTrianglesMappings(){

        List<Integer>[][] blocksTrianglesMappings = new List[numberOfXBlocks][numberOfXBlocks];

        for(int blockXIndex = 0; blockXIndex < numberOfXBlocks; blockXIndex++){

            for(int blockZIndex = 0; blockZIndex < numberOfZBlocks; blockZIndex++){

                blocksTrianglesMappings[blockXIndex][blockZIndex] = new ArrayList<>();
            }
        }

        logger.info("Initialized blocks triangles mappings");

        for(int triangleIndex = 0; triangleIndex < trianglesAABB.length; triangleIndex++){

            float[] triangleAABB = trianglesAABB[triangleIndex];
            float xDiff = triangleAABB[1] - triangleAABB[0];
            float yDiff = triangleAABB[3] - triangleAABB[2];
            int blockXIndex = (int) (xDiff / BLOCK_SIZE);
            int blockYIndex = (int) (yDiff / BLOCK_SIZE);

            blocksTrianglesMappings[blockXIndex][blockYIndex].add(triangleIndex);
        }

        logger.info("Loaded blocks triangles mappings");

        return blocksTrianglesMappings;
    }

    public static float[][] generate(float[] vertices, int[] faces){

        TerrainMeshHeightMapGenerator generator = new TerrainMeshHeightMapGenerator(vertices, faces);

        return generator.generateHeightMap();
    }

    public float[][] generateHeightMap() {

        logger.info("");
        logger.info("Started calculating height map for terrain");

        List<Callable<Object>> tasks = new ArrayList<>();

        for (int xI = 0; xI < numberOfXPoints; xI += BLOCK_SIZE) {

            for (int zI = 0; zI < numberOfZPoints; zI += BLOCK_SIZE) {

                int finalXI = xI;
                int finalZI = zI;

                Callable<Object> task = () -> {

                    generateHeightMap(finalXI, finalZI);

                    return null;
                };

                tasks.add(task);
            }
        }

        try {
            List<Future<Object>> futures = executor.invokeAll(tasks);

            for (var future : futures) {

                if (future.isCancelled()) {
                    throw new IllegalStateException("Nie udało się obliczyć mapy wysokości pod teren");
                }

                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        logger.info("Generated height map for terrain");

        return result;
    }

    private void generateHeightMap(int initXI, int initZI) {

        float[] barycentricParams = new float[3];
        Vector3f triangleA = new Vector3f();
        Vector3f triangleB = new Vector3f();
        Vector3f triangleC = new Vector3f();
        Vector3f point = new Vector3f();
        int maxXI = Math.min(initXI + BLOCK_SIZE, numberOfXPoints);
        int maxZI = Math.min(initXI + BLOCK_SIZE, numberOfZPoints);

        for(int xI = initXI; xI < maxXI; xI++){

            point.x = initXI + xI;

            for(int zI = 0; zI < maxZI; zI++){

                point.z = initZI + zI;

                appendToHeightMapForPoint(point, triangleA, triangleB, triangleC, barycentricParams);
            }
        }

        logger.info("Loaded height map for x, y " + initXI + ", " + initZI);
    }

    private void appendToHeightMapForPoint(
        Vector3f point, Vector3f triangleA, Vector3f triangleB, Vector3f triangleC, float[] barycentricParams
    ){
        float maxY = Float.MIN_VALUE;

        int blockXIndex = (int) Math.ceil(point.x / BLOCK_SIZE);
        int blockZIndex = (int) Math.ceil(point.z / BLOCK_SIZE);

        List<Integer> blockTriangles = blocksTrianglesMappings[blockXIndex][blockZIndex];

        for(int triangleIndex = 0, facesElementIndex = 0; triangleIndex < blockTriangles.size(); triangleIndex++, facesElementIndex += 3) {

            if(!blockTriangles.contains(triangleIndex)){
                continue;
            }

            loadTriangleVertex(facesElementIndex, triangleA);
            loadTriangleVertex(facesElementIndex + 1, triangleB);
            loadTriangleVertex(facesElementIndex + 2, triangleC);

            if(!isPointInsideTriangleAABB(point, triangleIndex)){
                continue;
            }

            loadBarycentricParams(triangleA, triangleB, triangleC, point, barycentricParams);
            float alpha = barycentricParams[0];
            float beta = barycentricParams[1];
            float gamma = barycentricParams[2];

            if(!isPointInsideTriangle(alpha, beta, gamma)){
                continue;
            }

            float y = getY(triangleA.y, triangleB.y, triangleC.y, alpha, beta, gamma);
            maxY = Math.max(y, maxY);
        }

        result[(int) point.x][(int) point.z] = maxY;
    }

    private void loadTriangleVertex(int vertexIndex, Vector3f resultVec){

        int vertexVerticesIndex = faces[vertexIndex] * 3;

        resultVec.x = vertices[vertexVerticesIndex];
        resultVec.y = vertices[vertexVerticesIndex + 1];
        resultVec.z = vertices[vertexVerticesIndex + 2];
    }

    private boolean isPointInsideTriangleAABB(Vector3f point, int triangleIndex){

        float[] triangleCoords = trianglesAABB[triangleIndex];

        return (point.x >= triangleCoords[0] && point.x <= triangleCoords[1]) &&
                (point.z >= triangleCoords[2] && point.z <= triangleCoords[3]);
    }

    private void loadBarycentricParams(Vector3f a, Vector3f b, Vector3f c, Vector3f point, float[] barycentricParams){

        float v0x = b.x - a.x;
        float v0z = b.z - a.z;
        float v1x = c.x - a.x;
        float v1z = c.z - a.z;
        float v2x = point.x - a.x;
        float v2z = point.z - a.z;

        float denomominator = v0x * v1z - v1x * v0z;

        if (denomominator == 0) {

            barycentricParams[0] = -1;
            barycentricParams[1] = -1;
            barycentricParams[2] = -1;

            return;
        }

        float alpha = (v2x * v1z - v1x * v2z) / denomominator;
        float beta = (v0x * v2z - v2x * v0z) / denomominator;
        float gamma = 1.0f - alpha - beta;

        barycentricParams[0] = alpha;
        barycentricParams[1] = beta;
        barycentricParams[2] = gamma;
    }

    private boolean isPointInsideTriangle(float alpha, float beta, float gamma){

        return (alpha >= 0f && alpha <= 1f) &&
                (beta >= 0f && beta <= 1f) &&
                (gamma >= 0f && gamma <= 1f);
    }

    private float getY(float aY, float bY, float cY, float alpha, float beta, float gamma){

        return alpha * aY + beta * bY + gamma * cY;
    }
}
