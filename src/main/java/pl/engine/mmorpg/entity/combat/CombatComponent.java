package pl.engine.mmorpg.entity.combat;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.input.InputComponent;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.entity.move.MoveState;

public class CombatComponent {

    private CombatState combatState = CombatState.NO_WEAPON;

    public void reset(){

        this.combatState = CombatState.NO_WEAPON;
    }

    public void update(InputComponent inputComponent, MoveComponent moveComponent, double deltaTime, Vector3f forward){

        if(inputComponent.combatStart){
            this.combatState = CombatState.FIGHTING;
        }
        else{
            this.combatState = CombatState.NO_WEAPON;
        }

        if(!isActive()){
            return;
        }

        moveComponent.moveForward(deltaTime / 2, forward);
    }

    public CombatState getCombatState(){

        return combatState;
    }

    public boolean isActive(){

        return combatState == CombatState.FIGHTING;
    }
}
