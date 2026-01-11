package pl.engine.mmorpg.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Shader {

    private final int shaderProgramId;
    private final Map<ShaderProps, Integer> shaderPropsIds = new HashMap<>();

    private static volatile Shader INSTANCE;

    private Shader(String vertexPath, String fragmentPath){

        shaderProgramId = load(vertexPath, fragmentPath);

        for(ShaderProps shaderProperty : ShaderProps.values()){

            int locationId = glGetUniformLocation(shaderProgramId, shaderProperty.getKey());

            shaderPropsIds.put(shaderProperty, locationId);
        }
    }

    public static Shader getInstance(){

        return INSTANCE;
    }

    public static Shader getInstance(String vertexPath, String fragmentPath){

        if(INSTANCE == null) {

            synchronized (Shader.class){

                if(INSTANCE == null){

                    INSTANCE = new Shader(vertexPath, fragmentPath);
                }
            }
        }

        return INSTANCE;
    }

    public void useShader(){

        glUseProgram(shaderProgramId);
    }

    public void setPropertyValue(ShaderProps shaderProperty, Object value) throws IllegalArgumentException, IllegalStateException{

        if(shaderProperty == null){

            throw new IllegalArgumentException("No shader property was given");
        }

        int locationId = shaderPropsIds.get(shaderProperty);

        shaderProperty.setValue(locationId, value);
    }

    private static int load(String vertexPath, String fragmentPath) throws IllegalStateException{

        String vertexSource = readFile(vertexPath);
        String fragmentSource = readFile(fragmentPath);

        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        int program = glCreateProgram();

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {

            throw new IllegalStateException("Program link error:\n" + glGetProgramInfoLog(program));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    private static int compileShader(int type, String source) throws IllegalStateException{

        int shader = glCreateShader(type);

        glShaderSource(shader, source);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {

            throw new IllegalStateException("Shader compile error:\n" + glGetShaderInfoLog(shader));
        }

        return shader;
    }

    private static String readFile(String filePath) throws IllegalStateException{

        try {

            URL gotUrl = Shader.class.getClassLoader().getResource(filePath);
            Path path = Path.of(gotUrl.toURI());

            return Files.readString(path);
        }
        catch (IOException | URISyntaxException e) {

            throw new IllegalStateException("Failed to load shader file: " + filePath, e);
        }
    }
}
