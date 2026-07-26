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

        Vector3f position = getPosition.get();

        entityStateData.isInAir = isInAir(position);

        if(entityStateData.isInAir){
            return;
        }

        double collisionMove = getCollisionMove(position);
        movementComponent.getVelocity().y += collisionMove;
    }

    public boolean isInAir(Vector3f position){

        if(terrainMesh.isOutside(position.x, position.z)){

            return false;
        }

        double maxValidY = terrainMesh.getTerrainMaxY(position.x, position.z);

        return position.y > maxValidY;
    }

    private double getCollisionMove(Vector3f position){

        double maxValidY = terrainMesh.getTerrainMaxY(position.x, position.z);

        if(position.y < maxValidY){

            return maxValidY - position.y;
        }

        return 0;
    }
}
