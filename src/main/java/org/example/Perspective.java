package org.example;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Perspective {

    private static int projectionId;

    public static void init(int width, int height){

        float aspectRatio = (float) width / (float) height;

        Matrix4f perspectiveMatrix = new Matrix4f().setPerspective((float)Math.toRadians(90), aspectRatio, 0.1f, 10000);

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        perspectiveMatrix.get(floatBuffer);

        glUniformMatrix4fv(projectionId, false, floatBuffer);
    }

    public static void setProjectionId(int projectionId){

        Perspective.projectionId = projectionId;
    }
}
