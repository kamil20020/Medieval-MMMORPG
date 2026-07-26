package pl.engine.mmorpg.entity.gravity;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.move.MovementComponent;

public class GravityMovementComponent implements Component {

    public static final double GRAVITY_SPEED = 0.12;

    private final MovementComponent movementComponent;

    public GravityMovementComponent(MovementComponent movementComponent){

        this.movementComponent = movementComponent;
    }

    @Override
    public void update(double deltaTime){

        movementComponent.getVelocity().y -= GRAVITY_SPEED;
    }
}
