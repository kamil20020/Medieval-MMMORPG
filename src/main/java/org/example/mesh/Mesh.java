package org.example.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;
import texture.Texture;

import static org.lwjgl.opengl.GL11.*;

public class Mesh {

    private final Vector3f[] vertices;
    private final Integer[] quads;
    private final Vector2f[] textureCords;
    private final Integer[] textureCordsForVertices;

    public Mesh(Vector3f[] vertices, Integer[] quads, Vector2f[] textureCords, Integer[] textureCordsForVertices){

        this.vertices = vertices;
        this.quads = quads;
        this.textureCords = textureCords;
        this.textureCordsForVertices = textureCordsForVertices;
    }

    public void draw(int[] textures, float xMin, float yMin, float zMin){

        for(int i = 0, textureIndex = 0; i < quads.length; i += 4, textureIndex++){

            int texture = textures[textureIndex];

            Texture.useTexture(texture);

            glBegin(GL_QUADS);

            for(int j = 0; j < 4; j++){

                int vIndex = quads[i + j];

                Vector3f v = vertices[vIndex];

                int vTextureCordsForVertexIndex = textureCordsForVertices[i + j];
                Vector2f textureCordsV = textureCords[vTextureCordsForVertexIndex];

                glTexCoord2f(textureCordsV.x, textureCordsV.y);
                glVertex3f(xMin + v.x, yMin + v.y, zMin + v.z);
            }

            glEnd();
        }
    }

    public Vector3f[] getVertices(){

        return vertices;
    }

    public Integer[] getQuads(){

        return quads;
    }

    public Vector2f[] getTextureCords(){

        return textureCords;
    }

    public Integer[] getTextureCordsForVertices(){

        return textureCordsForVertices;
    }

}
