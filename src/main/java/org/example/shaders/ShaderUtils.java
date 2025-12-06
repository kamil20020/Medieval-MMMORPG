package org.example.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ShaderUtils {

    public static int load(String vertexPath, String fragmentPath) throws IllegalStateException{

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

            URL gotUrl = ShaderUtils.class.getClassLoader().getResource(filePath);
            Path path = Path.of(gotUrl.toURI());

            return Files.readString(path);
        }
        catch (IOException | URISyntaxException e) {

            throw new IllegalStateException("Failed to load shader file: " + filePath, e);
        }
    }
}
