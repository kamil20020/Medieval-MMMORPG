package pl.engine.mmorpg.entity.ui.element;

import org.joml.Vector2f;
import pl.engine.mmorpg.entity.ui.element.UiElement;
import pl.engine.mmorpg.mesh.Rect;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;

public class SimpleUiElement implements UiElement {

    private Vector2f ownBottomLeftCorner;
    private Vector2f bottomLeftCorner;
    private float width;
    private float height;
    private float textureU;
    private float textureV;
    protected Texture texture;

    public SimpleUiElement(Vector2f ownBottomLeftCorner, float width, float height, float textureU, float textureV){

        this.ownBottomLeftCorner = ownBottomLeftCorner;
        this.bottomLeftCorner = new Vector2f(ownBottomLeftCorner);
        this.width = width;
        this.height = height;
        this.textureU = textureU;
        this.textureV = textureV;
    }

    @Override
    public void appendVertices(FloatBuffer verticesBuffer, Texture texture){

        appendVertex(verticesBuffer, 0, bottomLeftCorner.x, bottomLeftCorner.y);
        appendUv(verticesBuffer, 0);

        appendVertex(verticesBuffer, 1, bottomLeftCorner.x, bottomLeftCorner.y + height);
        appendUv(verticesBuffer, 1);

        appendVertex(verticesBuffer, 2, bottomLeftCorner.x + width, bottomLeftCorner.y + height);
        appendUv(verticesBuffer, 2);

        appendVertex(verticesBuffer, 3, bottomLeftCorner.x + width, bottomLeftCorner.y);
        appendUv(verticesBuffer, 3);
    }

    private void appendVertex(FloatBuffer verticesBuffer, int vertexIndex, float offsetX, float offsetY){

        float[] vertex = vertices[vertexIndex];

        verticesBuffer.put(vertex[0] + offsetX);
        verticesBuffer.put(vertex[1] + offsetY);
        verticesBuffer.put(vertex[2]);
    }

    private void appendUv(FloatBuffer verticesBuffer, int uvIndex){
        float[] uv = Rect.TEXTURE_COORDS[uvIndex];
        verticesBuffer.put(uv[0]);
        verticesBuffer.put(uv[1]);
    }

    @Override
    public void setParentOffset(Vector2f parentBottomLeftCorner){

        bottomLeftCorner = ownBottomLeftCorner.add(parentBottomLeftCorner);
    }

    public void update(double deltaTime){

    }
}
