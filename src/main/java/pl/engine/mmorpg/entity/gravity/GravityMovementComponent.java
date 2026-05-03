package pl.engine.mmorpg.entity.gravity;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.move.MoveComponent;

public class GravityMovementComponent {

    public static final double GRAVITY_SPEED = 0.12;

    private final MoveComponent moveComponent;

    public GravityMovementComponent(MoveComponent moveComponent){

        this.moveComponent = moveComponent;
    }

    public Vector3f update(Vector3f position){

        moveComponent.getVelocity().y -= GRAVITY_SPEED;

        position = position.add(moveComponent.getVelocity());

        double collisionMove = TerrainCollisionComponent.getInstance().getCollisionMove(position);
        moveComponent.getVelocity().y += collisionMove;

        return position;
    }
}
