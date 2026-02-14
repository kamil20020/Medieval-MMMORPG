package pl.engine.mmorpg.entity;

import org.joml.Vector3f;
import pl.engine.mmorpg.terrain.TerrainMesh;

public class GravityComponent {

    private final TerrainMesh terrainMesh;

    public GravityComponent(TerrainMesh terrainMesh){

        this.terrainMesh = terrainMesh;
    }

    public Vector3f getMove(Vector3f wantMove){

        return null;
    }
}
