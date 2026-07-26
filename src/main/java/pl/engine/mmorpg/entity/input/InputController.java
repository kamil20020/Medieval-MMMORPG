package pl.engine.mmorpg.entity.input;

public abstract class InputController {

    protected final InputComponent inputComponent;

    public InputController(){

        this.inputComponent = new InputComponent();
    }

    public InputComponent getInputComponent(){

        return inputComponent;
    }

    public abstract void update();
}
