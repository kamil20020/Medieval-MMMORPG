package org.example.mesh;

import org.joml.Matrix4f;

public interface Meshable {

    void uploadToGpu();
    void setModel(Matrix4f model);
    void draw();
    void clear();
    void update(double deltaTimeInSeconds);
}
