package pl.engine.mmorpg.mesh;

import org.joml.Matrix4f;

public interface Meshable {

    void uploadToGpu();
    void setModel(Matrix4f model);
    void draw();
    void clear();
    void update(double deltaTimeInSeconds);
    int getNumberOfVertices();
    int getNumberOfFaces();
    float[] getVertices();
    int[] getFaces();
}
