package pl.engine.mmorpg.entity.ui;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderType;
import pl.engine.mmorpg.shaders.Shaders;
import pl.engine.mmorpg.shaders.props.MeshShaderProps;
import pl.engine.mmorpg.shaders.props.UiShaderProps;
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

    private final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(NUMBER_OF_VERTICES * VERTEX_SIZE);

    private static final int MAX_NUMBER_OF_UI_ELEMENTS = 50;
    private static final int NUMBER_OF_VERTICES = MAX_NUMBER_OF_UI_ELEMENTS * 4;
    private static final int NUMBER_OF_FACES = MAX_NUMBER_OF_UI_ELEMENTS * 2;
    private static final int VERTEX_SIZE = 3;
    private static final int STRIDE = VERTEX_SIZE * Float.BYTES;

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

        Shader uiShader = Shaders.getShader(ShaderType.UI);
        uiShader.setPropertyValue(UiShaderProps.IS_GIVEN_COLOR, true);
        uiShader.setPropertyValue(UiShaderProps.COLOR, new Vector4f(0, 1, 1, 1));

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

        Shader uiShader = Shaders.getShader(ShaderType.UI);
        uiShader.setPropertyValue(UiShaderProps.IS_GIVEN_COLOR, false);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
    }

    public void clear(){

        glDeleteBuffers(vertexBufferId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vertexArraysId);
    }

    public void addUiElement(UIElement uiElement){

        uiElements.add(uiElement);
    }
}
