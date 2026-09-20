package pl.engine.mmorpg.render;

import org.joml.Matrix4f;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.libraries.jgltf.JgltfMeshAbstractFactory;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderType;
import pl.engine.mmorpg.shaders.Shaders;
import pl.engine.mmorpg.shaders.props.MeshShaderProps;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Window window;
    private final MeshAbstractFactory meshFactory;
    private final Chunk chunk;

    public Renderer(Window window, EventsHandler eventsHandler){

        this.window = window;
        this.meshFactory = new JgltfMeshAbstractFactory();
        this.chunk = new Chunk(window, eventsHandler, meshFactory);
    }

    public void init(){

        chunk.init();
        initTextures();
    }

    private void initTextures(){

        glEnable(GL_TEXTURE_2D);

        Shader meshShader = Shaders.getShader(ShaderType.MESH);

        meshShader.setPropertyValue(MeshShaderProps.TEXTURE0, 0);

        Matrix4f identityMatrix = new Matrix4f().identity();
        meshShader.setPropertyValue(MeshShaderProps.MODEL, identityMatrix);

        meshShader.setPropertyValue(MeshShaderProps.IS_ANIMATED, Boolean.TRUE);
    }

    public void update(double deltaTime){

        chunk.update(deltaTime);
    }

    public void render(){

        chunk.draw();
    }

    public void clear(){

        chunk.clear();
    }
}
