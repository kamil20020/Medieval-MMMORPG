package pl.engine.mmorpg.entity.combat;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.entity.move.MovementComponent;

public class WarriorDodgeSkill extends Skill{

    private final InputData inputData;
    private final EntityStateData entityStateData;
    private final MovementComponent movementComponent;
    private final TransformComponent transformComponent;

    public WarriorDodgeSkill(InputData inputData, EntityStateData entityStateData, MovementComponent movementComponent, TransformComponent transformComponent) {

        super(SkillType.WARRIOR_DODGE, 0);

        this.inputData = inputData;
        this.entityStateData = entityStateData;
        this.movementComponent = movementComponent;
        this.transformComponent = transformComponent;
    }

    @Override
    public void update(double deltaTime) {

        Vector3f forward = transformComponent.getForward();

        if(inputData.moveBack){
            movementComponent.moveBackward(1, forward);
            entityStateData.entityState = EntityState.MOVE;
        }
        else if(inputData.moveLeft){
            movementComponent.moveLeft(1, forward);
            entityStateData.entityState = EntityState.MOVE;
        }
        else if(inputData.moveRight){
            movementComponent.moveRight(1, forward);
            entityStateData.entityState = EntityState.MOVE;
        }
    }

    @Override
    public boolean hasOwnAnimation(){

        return false;
    }
}
