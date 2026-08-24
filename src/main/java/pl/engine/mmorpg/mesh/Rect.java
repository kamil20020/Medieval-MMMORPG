package pl.engine.mmorpg.mesh;

import pl.engine.mmorpg.texture.Texture;

public class Rect extends SimpleMesh{

    private static final float[][] vertices = {
        {0, 0.1f, 0},
        {0, 0.1f, 0.5f},
        {0.5f, 0.1f, 0.5f},
        {0.5f, 0.1f, 0}
    };
    /*
   1 ______ 2
     |   /|
     |  / |
     | /  |
   0 |/___| 3
     */
    private static final int[][] faces = {
        {0, 2, 1},
        {3, 2, 0}
    };
    public static float[][] TEXTURE_COORDS = new float[][]{
        {0, 0},
        {0, 1},
        {1, 1},
        {1, 0}
    };

    public Rect(Texture texture){

        super(vertices, faces, texture);
    }
}
