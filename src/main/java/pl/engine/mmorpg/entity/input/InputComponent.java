package pl.engine.mmorpg.entity.input;

import pl.engine.mmorpg.entity.Component;

public abstract class InputComponent implements Component {

    protected final InputData inputData;

    public InputComponent(){

        this.inputData = new InputData();
    }

    public InputData getInputData(){

        return inputData;
    }
}
