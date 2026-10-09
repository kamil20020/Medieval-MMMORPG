package pl.engine.mmorpg.entity.ui;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import pl.engine.mmorpg.entity.ui.element.UiElement;
import pl.engine.mmorpg.mesh.Rect;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderType;
import pl.engine.mmorpg.shaders.Shaders;
import pl.engine.mmorpg.shaders.props.UiShaderProps;
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

    private final List<UiElement> uiElements = new ArrayList<>();

    private final IntBuffer indicesBuffer = BufferUtils.createIntBuffer(NUMBER_OF_FACES * 3);
    private final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(NUMBER_OF_VERTICES * VERTEX_SIZE);

    private static final int MAX_NUMBER_OF_UI_ELEMENTS = 100;
    private static final int NUMBER_OF_VERTICES = MAX_NUMBER_OF_UI_ELEMENTS * 4;
    private static final int NUMBER_OF_FACES = MAX_NUMBER_OF_UI_ELEMENTS * 2;
    private static final int VERTEX_SIZE = 5;
    private static final int STRIDE = VERTEX_SIZE * Float.BYTES;

    private static final String UI_TEXTURE_FILE_PATH = "textures/ui.png";

    public void init(){

        this.texture = new FileTexture(UI_TEXTURE_FILE_PATH, Rect.TEXTURE_COORDS);

        fillIndicesBuffer();

        vertexArraysId = glGenVertexArrays();
        glBindVertexArray(vertexArraysId);

        bindVerticesBuffer();
        bindEboBuffer(indicesBuffer);
    }

    private void fillIndicesBuffer(){

        indicesBuffer.clear();

        for(int i = 0; i < MAX_NUMBER_OF_UI_ELEMENTS; i++){

            UiElement.appendIndices(indicesBuffer, i);
        }

        indicesBuffer.flip();
    }

    private void bindVerticesBuffer(){

        vertexBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, STRIDE, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
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
//        uiShader.setPropertyValue(UiShaderProps.IS_GIVEN_COLOR, true);
        uiShader.setPropertyValue(UiShaderProps.COLOR, new Vector4f(0, 1, 1, 1));

        if(texture != null){
            Texture.useTexture(texture.getId());
        }

        uploadVerticesToGpu();

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
    }

    private void uploadVerticesToGpu(){

        verticesBuffer.clear();

        for(UiElement uiElement : uiElements){

            uiElement.appendVertices(verticesBuffer, texture);
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

    public void addUiElement(UiElement uiElement){

        uiElements.add(uiElement);
    }

    public void update(double deltaTime){

        for(UiElement uiElement : uiElements){

            uiElement.update(deltaTime);
        }
    }
}
