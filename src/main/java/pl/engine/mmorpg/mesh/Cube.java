package pl.engine.mmorpg.mesh;

import pl.engine.mmorpg.texture.Texture;

public class Cube extends SimpleMesh{

    private static final float[][] vertices = {
        {-5, 0, -5}, // lewy przód,
        {5, 0, -5},  // prawy przód,
        {5, 0, 5},   // prawy tył,
        {-5, 0, 5}   // lewy tył,
    };
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

    public Cube(Texture texture){

        super(vertices, faces, texture);
    }
}
