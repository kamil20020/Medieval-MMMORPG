package pl.engine.mmorpg.entity.gravity;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Entity;
import pl.engine.mmorpg.entity.TerrainCollisionComponent;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.entity.move.MoveState;
import pl.engine.mmorpg.terrain.TerrainMesh;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class GravityMovementComponent {

    private double timeStartInAir = 0;

    private final MoveComponent moveComponent;

    public GravityMovementComponent(MoveComponent moveComponent){

        this.moveComponent = moveComponent;
    }

    public void update(double deltaTime){

        moveComponent.getVelocity().y += GravityComponent.GRAVITY_SPEED;
    }
}
