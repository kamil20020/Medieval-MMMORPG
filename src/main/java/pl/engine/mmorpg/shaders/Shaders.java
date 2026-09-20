package pl.engine.mmorpg.shaders;

import pl.engine.mmorpg.shaders.props.MeshShaderProps;
import pl.engine.mmorpg.shaders.props.UiShaderProps;

import java.util.HashMap;
import java.util.Map;

public class Shaders {

    private static Map<ShaderType, Shader> shaders = new HashMap<>();

    private static volatile boolean isInitialized = false;

     synchronized public static void init(){

        if(isInitialized){
         return;
        }

        addShaders();

        isInitialized = true;
    }

    private static void addShaders(){

        Shader meshShader = new Shader(
            "shaders/mesh/vertex.vert",
            "shaders/mesh/fragment.frag",
            MeshShaderProps.values()
        );
        shaders.put(ShaderType.MESH, meshShader);

        Shader uiShader = new Shader(
            "shaders/ui/vertex.vert",
            "shaders/ui/fragment.frag",
            UiShaderProps.values()
        );
        shaders.put(ShaderType.UI, uiShader);
    }

    public static Shader getShader(ShaderType shaderType) throws IllegalStateException{

        if(!shaders.containsKey(shaderType)){
            throw new IllegalStateException("Shader of type " + shaderType.name() + " does not exist");
        }

        Shader shader = shaders.get(shaderType);
        shader.useShader();

        return shader;
    }
}
