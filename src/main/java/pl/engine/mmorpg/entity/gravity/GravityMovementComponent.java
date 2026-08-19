package pl.engine.mmorpg.entity.gravity;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.terrain.TerrainMesh;

import java.util.function.Supplier;

public class GravityMovementComponent implements Component {

    private double airStartTime = 0d;

    public static final double GRAVITY_SPEED = 0.1d;

    private final MovementComponent movementComponent;
    private final EntityStateData entityStateData;
    private final TerrainMesh terrainMesh;
    private final Supplier<Vector3f> getPosition;

    public GravityMovementComponent(MovementComponent movementComponent, EntityStateData entityStateData, Supplier<Vector3f> getPosition){

        this.movementComponent = movementComponent;
        this.entityStateData = entityStateData;
        this.terrainMesh = TerrainMesh.getInstance();
        this.getPosition = getPosition;
    }

    @Override
    public void update(double deltaTime){

        Vector3f position = getPosition.get();
        Vector3f speed = movementComponent.getVelocity();
        Vector3f updatedPosition = position.add(speed);

        entityStateData.isInAir = terrainMesh.terrainPointVerticalDifference(updatedPosition) < -0.5d;

        if(!entityStateData.isInAir){

            movementComponent.getVelocity().y = 0;
            airStartTime = 0d;

            if(entityStateData.entityState == EntityState.FALLING) {

                entityStateData.entityState = EntityState.STANDING;
                entityStateData.canActionBeInterrupted = true;
            }
            return;
        }

        entityStateData.entityState = EntityState.FALLING;
        entityStateData.canActionBeInterrupted = false;

        if(airStartTime == 0){
            airStartTime = System.nanoTime();
        }

        speed.y -= getGravityVelocity();

        if(speed.y < 0){

            entityStateData.entityState = EntityState.FALLING;
        }
    }

    private double getGravityVelocity(){

        double actualTime = System.nanoTime();
        double timeInAirInSeconds = (actualTime - airStartTime) / 1_000_000_000d;
        return GRAVITY_SPEED * (timeInAirInSeconds + 1d);
    }
}
