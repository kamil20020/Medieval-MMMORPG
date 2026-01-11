package pl.engine.mmorpg.render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Perspective {

    public static void init(int width, int height){

        float aspectRatio = (float) width / (float) height;

        Matrix4f perspectiveMatrix = new Matrix4f().setPerspective((float)Math.toRadians(90), aspectRatio, 0.1f, 10000);

        Shader shader = Shader.getInstance();
        shader.setPropertyValue(ShaderProps.PERSPECTIVE, perspectiveMatrix);
    }
}
