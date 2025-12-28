package org.example;

import org.example.shaders.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer {

    private final Window window;
    private final Camera camera;
    private final Chunk chunk;
    public static int shaderProgramId;
    public static int modelId;

    public static int isAnimatedId;
    public static int vertexBoneIndicesId;
    public static int vertexBoneWeightsId;
    public static int finalBoneMatricesId;
    public static int viewId;
    public static int useOutsideColorId;
    public static int outsideColorId;

    public Renderer(Window window, EventsHandler eventsHandler){

        this.window = window;
        this.camera = new Camera(new Vector3f(0, 2, -2));
        this.chunk = new Chunk(modelId, isAnimatedId, camera, window, eventsHandler);
    }

    public void init(int shaderId){

        shaderProgramId = shaderId;
        chunk.init();
        initTextures();
    }

    private void initTextures(){

        glEnable(GL_TEXTURE_2D);

        int texLocation = glGetUniformLocation(shaderProgramId, "texture0");
        glUniform1i(texLocation, 0); // GL_TEXTURE0

        modelId = glGetUniformLocation(shaderProgramId, "model");
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        Matrix4f identityMatrix = new Matrix4f().identity();
        FloatBuffer initModelBuffer = identityMatrix.get(buffer);
        glUniformMatrix4fv(modelId, false, initModelBuffer);

        isAnimatedId = glGetUniformLocation(shaderProgramId, "isAnimated");
        glUniform1i(isAnimatedId, 1);

        finalBoneMatricesId = glGetUniformLocation(shaderProgramId, "finalBoneMatrices");

        viewId = glGetUniformLocation(shaderProgramId, "view");

        useOutsideColorId = glGetUniformLocation(shaderProgramId, "useOutsideColor");

        outsideColorId = glGetUniformLocation(shaderProgramId, "outsideColor");
    }

    public void render(double deltaTime){

        camera.update(viewId);

        chunk.draw(deltaTime);
    }

    public void clear(){

        chunk.clear();
    }
}
