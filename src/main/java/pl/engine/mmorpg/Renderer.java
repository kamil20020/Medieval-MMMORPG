package pl.engine.mmorpg;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer {

    private final Window window;
    private final Camera camera;
    private final Chunk chunk;

    public Renderer(Window window, EventsHandler eventsHandler){

        this.window = window;
        this.camera = new Camera(new Vector3f(0, 2, -2));
        this.chunk = new Chunk(camera, window, eventsHandler);
    }

    public void init(){

        chunk.init();
        initTextures();
    }

    private void initTextures(){

        glEnable(GL_TEXTURE_2D);

        Shader shader = Shader.getInstance();

        shader.setPropertyValue(ShaderProps.TEXTURE0, 0);

        Matrix4f identityMatrix = new Matrix4f().identity();
        shader.setPropertyValue(ShaderProps.MODEL, identityMatrix);

        shader.setPropertyValue(ShaderProps.IS_ANIMATED, Boolean.TRUE);
    }

    public void render(double deltaTime){

        camera.update();

        chunk.draw(deltaTime);
    }

    public void clear(){

        chunk.clear();
    }
}
