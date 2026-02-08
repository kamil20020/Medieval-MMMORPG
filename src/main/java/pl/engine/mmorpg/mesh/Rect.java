package pl.engine.mmorpg.mesh;

import pl.engine.mmorpg.texture.Texture;

public class Rect extends SimpleMesh{

    private static final float[][] vertices = {
        {-74, 0, -43}, // lewy przód,
        {52, 0, -43},  // prawy przód,
        {52, 0, 76},   // prawy tył,
        {-74, 0, 76}   // lewy tył,
    };
    /*
   3 ______ 2
     |   /|
     |  / |
     | /  |
   0 |/___| 1
     */
    private static final int[][] faces = {
        {0, 2, 1}, // pierwszy trójkąt (lewy przód, prawy tył, prawy przód)
        {3, 2, 0}  // drugi trójkąt (lewy tył, prawy tył, lewy przód)
    };
    public static float[][] TEXTURE_COORDS = new float[][]{
        {0, 50},
        {50, 50},
        {50, 0},
        {0, 0}
    };

    public Rect(Texture texture){

        super(vertices, faces, texture);
    }
}
