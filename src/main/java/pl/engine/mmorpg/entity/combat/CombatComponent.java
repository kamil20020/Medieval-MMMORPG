package pl.engine.mmorpg.entity.combat;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.entity.move.MovementComponent;

import java.util.Arrays;

public class CombatComponent implements Component {

    private final InputData inputData;
    private final EntityStateData entityStateData;
    private final MovementComponent movementComponent;
    private final TransformComponent transformComponent;

    private double comboStartTime = 0;
    private int actualComboPartIndex = 0;
    private boolean isStartedCombo = false;

    private static final double[] comboDurations = {1d, 0.5d, 1.5d};

    private static final double[] comboIncreasingDurations = getComboIncreasingDurations();
    private static final double comboDurationsSum = Arrays.stream(comboDurations).sum();

    private static double[] getComboIncreasingDurations(){

        double[] result = new double[comboDurations.length];

        double actualDuration = 0;

        for(int i = 0; i < result.length; i++){

            double comboPartDuration = comboDurations[i];

            actualDuration += comboPartDuration;
            result[i] = actualDuration;
        }

        return result;
    }

    public CombatComponent(InputData inputData, EntityStateData entityStateData, MovementComponent movementComponent, TransformComponent transformComponent){

        this.inputData = inputData;
        this.entityStateData = entityStateData;
        this.movementComponent = movementComponent;
        this.transformComponent = transformComponent;
    }

    public void update(double deltaTime){

        if(!entityStateData.canActionBeInterrupted && !isStartedCombo){

            comboStartTime = 0;
            actualComboPartIndex = 0;
            return;
        }

        if(!inputData.combatStart){

            comboStartTime = 0;
            actualComboPartIndex = 0;
            isStartedCombo = false;
            return;
        }

        Vector3f forward = transformComponent.getForward();
        movementComponent.moveForward(deltaTime / 3, forward);

        handleComboAnimations();
    }

    private void startCombo(){

        isStartedCombo = true;
        entityStateData.entityState = EntityState.COMBAT;
        entityStateData.canActionBeInterrupted = false;
        entityStateData.actionMinimumDuration = comboDurations[0];
    }

    private void handleComboAnimations(){

        if(comboStartTime == 0){

            startCombo();
            comboStartTime = System.nanoTime();
            return;
        }

        double actualComboPartIncreasingDuration = getComboPartDuration();
        double comboPartIncreasingDuration = comboIncreasingDurations[actualComboPartIndex];

        if(actualComboPartIncreasingDuration < comboPartIncreasingDuration) {
            return;
        }

        actualComboPartIndex = (actualComboPartIndex + 1) % comboDurations.length;
        entityStateData.actionMinimumDuration = comboIncreasingDurations[actualComboPartIndex];
    }

    private double getComboPartDuration(){

        double actualTime = System.nanoTime();
        double result = (actualTime - comboStartTime) / 1_000_000_000d;

        return result % comboDurationsSum;
    }
}
