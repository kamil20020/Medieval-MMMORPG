package pl.engine.mmorpg.entity.combat;

public class CombatComponent {

    private CombatState combatState = CombatState.NO_WEAPON;

    public void reset(){

        this.combatState = CombatState.NO_WEAPON;
    }

    public CombatState getCombatState(){

        return combatState;
    }
}
