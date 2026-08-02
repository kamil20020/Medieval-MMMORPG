package pl.engine.mmorpg.entity.input;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityStateData;

public class ActionsComponent implements Component {

    private final InputData inputData;
    private final EntityStateData entityStateData;

    public ActionsComponent(InputData inputData, EntityStateData entityStateData){

        this.inputData = inputData;
        this.entityStateData = entityStateData;
    }

    @Override
    public void update(double deltaTimeInSeconds){

        if(inputData.gravitySwitchPressed){
            entityStateData.isGravityEnabled = !entityStateData.isGravityEnabled;
        }
    }
}
