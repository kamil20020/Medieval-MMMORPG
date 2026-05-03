package pl.engine.mmorpg.entity.combat;

import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.entity.move.MoveState;

public class CombatComponent {

    private CombatState combatState = CombatState.NO_WEAPON;

    public void reset(){

        this.combatState = CombatState.NO_WEAPON;
    }

    public CombatState getCombatState(){

        return combatState;
    }

    public void startFight(){

        combatState = CombatState.FIGHTING;
    }

    public void endFight(){

        combatState = CombatState.NO_WEAPON;
    }
}
