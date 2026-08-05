package pl.engine.mmorpg.entity.gravity;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.terrain.TerrainMesh;

import java.util.function.Supplier;

public class GravityMovementComponent implements Component {

    private final TerrainMesh terrainMesh;
    private final Supplier<Vector3f> getPosition;

    private double airStartTime = 0d;

    public static final double GRAVITY_SPEED = 0.12d;

    private final MovementComponent movementComponent;

    public GravityMovementComponent(MovementComponent movementComponent, Supplier<Vector3f> getPosition){

        this.terrainMesh = TerrainMesh.getInstance();
        this.movementComponent = movementComponent;
        this.getPosition = getPosition;
    }

    @Override
    public void update(double deltaTime){

        Vector3f position = getPosition.get();
        position = position.add(movementComponent.getVelocity());

        if(!terrainMesh.isInAirForPoint(position)){
            airStartTime = 0;
            return;
        }

        if(airStartTime == 0){
            airStartTime = System.nanoTime();
        }

        double actualTime = System.nanoTime();
        double timeInAirInSeconds = (actualTime - airStartTime) / 1_000_000_000d;

        movementComponent.getVelocity().y -= GRAVITY_SPEED * (timeInAirInSeconds + 1d);
    }
}
