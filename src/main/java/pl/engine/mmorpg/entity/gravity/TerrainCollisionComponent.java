package pl.engine.mmorpg.entity.gravity;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.terrain.TerrainMesh;

import java.util.function.Supplier;

public class TerrainCollisionComponent implements Component {

    private final TerrainMesh terrainMesh;
    private final EntityStateData entityStateData;
    private final MovementComponent movementComponent;
    private final Supplier<Vector3f> getPosition;

    public TerrainCollisionComponent(EntityStateData entityStateData, MovementComponent movementComponent, Supplier<Vector3f> getPosition){

        this.terrainMesh = TerrainMesh.getInstance();
        this.entityStateData = entityStateData;
        this.movementComponent = movementComponent;
        this.getPosition = getPosition;
    }

    @Override
    public void update(double deltaTime){

        if(entityStateData.isInAir){
            return;
        }

        Vector3f position = getPosition.get();
        position = position.add(movementComponent.getVelocity());

        if(entityStateData.isInAir){
            return;
        }

        double collisionMove = terrainMesh.terrainPointVerticalDifference(position);
        movementComponent.getVelocity().y += collisionMove;
    }
}
