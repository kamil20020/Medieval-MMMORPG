package org.example;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;

public class Perspective {

    public static void init(int width, int height){

        float aspectRatio = (float) width / (float) height;

        Matrix4f perspectiveMatrix = new Matrix4f().setPerspective(90, aspectRatio, 0.1f, 10000);

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        perspectiveMatrix.get(floatBuffer);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glLoadMatrixf(floatBuffer);
    }
}
