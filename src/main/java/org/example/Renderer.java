package org.example;

import org.example.shaders.ShaderUtils;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer {

    private final Window window;
    private final Camera camera;
    private final Chunk chunk;
    private int shaderProgramId;

    public Renderer(Window window, EventsHandler eventsHandler){

        this.window = window;
        this.camera = new Camera(new Vector3f(0, 0, 0), eventsHandler);
        this.chunk = new Chunk();
    }

    public void init(){

        chunk.init();
        initTextures();
    }

    private void initTextures(){

        glEnable(GL_TEXTURE_2D);

        shaderProgramId = ShaderUtils.load("shaders/vertex.glsl", "shaders/fragment.glsl");
        glUseProgram(shaderProgramId);

        int texLocation = glGetUniformLocation(shaderProgramId, "texture0");
        glUniform1i(texLocation, 0); // GL_TEXTURE0
    }

    public void render(){

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        camera.update();

        glUseProgram(shaderProgramId);

        chunk.draw();
    }
}
