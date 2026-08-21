package pl.engine.mmorpg.entity.combat;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.move.MovementComponent;

public class WarriorSpinSkill extends Skill{

    private final MovementComponent movementComponent;
    private final TransformComponent transformComponent;

    public WarriorSpinSkill(MovementComponent movementComponent, TransformComponent transformComponent) {

        super(SkillType.WARRIOR_SPIN, 1.3);

        this.movementComponent = movementComponent;
        this.transformComponent = transformComponent;
    }

    @Override
    public void update(double deltaTime) {

        Vector3f forward = transformComponent.getForward();
        movementComponent.moveForward(deltaTime / 2, forward);
    }
}
