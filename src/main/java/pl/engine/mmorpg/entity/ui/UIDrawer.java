package pl.engine.mmorpg.entity.ui;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.mesh.Rect;
import pl.engine.mmorpg.render.Window;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;
import pl.engine.mmorpg.texture.FileTexture;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class UIDrawer {

    protected int vertexArraysId;
    protected int vertexBufferId;
    private int eboId;
    private Texture texture;

    private List<UIElement> uiElements = new ArrayList<>();

    private FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(NUMBER_OF_VERTICES * VERTEX_SIZE);

    private static final int MAX_NUMBER_OF_UI_ELEMENTS = 50;
    private static final int NUMBER_OF_VERTICES = MAX_NUMBER_OF_UI_ELEMENTS * 4;
    private static final int NUMBER_OF_FACES = MAX_NUMBER_OF_UI_ELEMENTS * 2;
    private static final int VERTEX_SIZE = 3;
    private static final int STRIDE = VERTEX_SIZE * Float.BYTES;

    private final Window window;

    public UIDrawer(Window window){

        this.window = window;

//        this.texture = new FileTexture("textures/ground-test.png", Rect.TEXTURE_COORDS);
//        this.rect = new Rect(texture);

        UIElement uiElement = new UIElement(new Vector2f(0, 0), window.getWidth(), 50, 0, 1);
        uiElements.add(uiElement);
    }

    public void init(){

        IntBuffer indicesBuffer = initIndicesBuffer();

        vertexArraysId = glGenVertexArrays();
        glBindVertexArray(vertexArraysId);

        bindVerticesBuffer();
        bindEboBuffer(indicesBuffer);
    }

    private IntBuffer initIndicesBuffer(){

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(NUMBER_OF_FACES * 3);

        for(int i = 0; i < MAX_NUMBER_OF_UI_ELEMENTS; i++){

            UIElement.appendIndices(indicesBuffer);
        }

        indicesBuffer.flip();

        return indicesBuffer;
    }

    private FloatBuffer initVerticesBuffer(){

        //3 - x, y, z, 2 - uv texture, 3 - normals
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(NUMBER_OF_VERTICES * VERTEX_SIZE);

        for(UIElement uiElement : uiElements){

            uiElement.uploadToGpu(verticesBuffer, texture);
        }

        verticesBuffer.flip();

        return verticesBuffer;
    }

    private void bindVerticesBuffer(){

        vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0);
        glEnableVertexAttribArray(0);

//        glVertexAttribPointer(1, 2, GL_FLOAT, false, STRIDE, 3 * Float.BYTES);
//        glEnableVertexAttribArray(1);
    }

    private void bindEboBuffer(IntBuffer indicesBuffer){

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    public void draw(){

        beforeDraw();

        glBindVertexArray(vertexArraysId);
        glDrawElements(GL_TRIANGLES, NUMBER_OF_FACES * 3, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        afterDraw();
    }

    private void beforeDraw(){

        Shader shader = Shader.getInstance();
        shader.setPropertyValue(ShaderProps.IS_DISABLED_LIGHT, true);
        shader.setPropertyValue(ShaderProps.IS_GIVEN_COLOR, true);
        shader.setPropertyValue(ShaderProps.COLOR, new Vector4f(0, 1, 1, 1));
        shader.setPropertyValue(ShaderProps.IS_DRAWING_UI, true);

        if(texture != null){
//            Texture.useTexture(rect.getTexture().getId());
        }

        uploadVerticesToGpu();

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
    }

    private void uploadVerticesToGpu(){

        verticesBuffer.clear();

        for(UIElement uiElement : uiElements){

            uiElement.uploadToGpu(verticesBuffer, texture);
        }

        verticesBuffer.flip();

        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    }

    private void afterDraw(){

        Shader shader = Shader.getInstance();
        shader.setPropertyValue(ShaderProps.IS_DISABLED_LIGHT, false);
        shader.setPropertyValue(ShaderProps.IS_GIVEN_COLOR, false);
        shader.setPropertyValue(ShaderProps.IS_DRAWING_UI, false);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    public void clear(){

        glDeleteBuffers(vertexBufferId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vertexArraysId);
    }
}
