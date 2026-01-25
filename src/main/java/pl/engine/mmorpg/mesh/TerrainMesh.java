package pl.engine.mmorpg.mesh;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TerrainMesh implements Meshable{

    private final float[] vertices;
    private final int[] faces;
    private final ComplexMesh mesh;

    public TerrainMesh(String terrainFilePath, MeshAbstractFactory meshFactory){

        this.mesh = meshFactory.createComplexMesh(terrainFilePath);
        this.vertices = mesh.getVertices();
        this.faces = mesh.getFaces();
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

    public final float[][] generateHeightMap(){

        Vector3f[] minAndMaxCoords = getMinAndMaxCoords();
        Vector3f minCoords = minAndMaxCoords[0];
        Vector3f maxCoords = minAndMaxCoords[1];

        int numberOfXPoints = (int) (maxCoords.x - minCoords.x);
        int numberOfZPoints = (int) (maxCoords.z - minCoords.z);
        float[][] result = new float[numberOfXPoints][numberOfZPoints];
        Vector3f point = new Vector3f();

        for(int xI = 0; xI < result.length; xI++){

            point.x = xI;

            for(int zI = 0; zI < result[xI].length; zI++){

                point.z = zI;
                appendToHeightMapForPoint(xI, zI, result, point);
            }
        }

        return result;
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

        return new Vector3f[]{min, max};
    }

    private void appendToHeightMapForPoint(int xI, int zI, float[][] result, Vector3f point){

        float maxY = Float.MIN_VALUE;

        for(int facesElementIndex = 0; facesElementIndex < faces.length; facesElementIndex += 3) {

            Vector3f triangleA = getTriangleVertex(facesElementIndex);
            Vector3f triangleB = getTriangleVertex(facesElementIndex + 1);
            Vector3f triangleC = getTriangleVertex(facesElementIndex + 2);

            float[] barycentricParams = getBarycentricParams(triangleA, triangleB, triangleC, point);
            float alpha = barycentricParams[0];
            float beta = barycentricParams[1];
            float gamma = barycentricParams[2];

            if(!isPointInsideTriangle(alpha, beta, gamma)){
                continue;
            }

            float y = getY(triangleA.y, triangleB.y, triangleC.y, alpha, beta, gamma);
            maxY = Math.max(y, maxY);
        }

        result[xI][zI] = maxY;
    }

    private Vector3f getTriangleVertex(int vertexIndex){

        Vector3f result = new Vector3f();

        int vertexVerticesIndex = faces[vertexIndex] * 3;
        result.x = vertices[vertexVerticesIndex];
        result.y = vertices[vertexVerticesIndex + 1];
        result.z = vertices[vertexVerticesIndex + 2];

        return result;
    }

    private float[] getBarycentricParams(Vector3f triangleA, Vector3f triangleB, Vector3f triangleC, Vector3f point){

        Vector3f vecBA = new Vector3f(triangleB).sub(triangleA);
        Vector3f vecCA = new Vector3f(triangleC).sub(triangleA);
        float areaABC = vecBA.cross(vecCA).length() / 2f;

        Vector3f vecBP = new Vector3f(triangleB).sub(point);
        Vector3f vecCP = new Vector3f(triangleC).sub(point);
        float areaPBC = vecBP.cross(vecCP).length() / 2f;

        Vector3f vecAP = new Vector3f(triangleC).sub(point);
        float areaPCA = vecCP.cross(vecAP).length() / 2f;

        float alpha = areaPBC / areaABC;
        float beta = areaPCA / areaABC;
        float gamma = 1f - alpha - beta;

        return new float[]{alpha, beta, gamma};
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
