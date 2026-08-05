package pl.engine.mmorpg.entity.combat;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.input.InputData;

public class CombatComponent implements Component {

    private CombatState combatState = CombatState.NO_WEAPON;
    private final InputData inputData;

    private double stateCloseDelta;
    public static final double STATE_CLOSE_TIME = 0.5;
    private boolean wantFight = false;

    public CombatComponent(InputData inputData){

        this.inputData = inputData;
    }

    public void reset(){

        this.combatState = CombatState.NO_WEAPON;
    }

    public void update(double deltaTime){

        if(inputData.combatStart){
            wantFight = true;
        }
        else{
            wantFight = false;
        }

        if(!isActive()){
            return;
        }

        inputData.moveFront = true;
    }

    public CombatState getCombatState(){

        return combatState;
    }

    public boolean isActive(){

        return combatState == CombatState.FIGHTING;
    }
}
