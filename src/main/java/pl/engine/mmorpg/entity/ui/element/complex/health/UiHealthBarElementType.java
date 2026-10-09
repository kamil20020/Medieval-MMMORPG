package pl.engine.mmorpg.entity.ui.element.complex.health;

import pl.engine.mmorpg.entity.ui.element.complex.ComplexUiElementType;

public enum UiHealthBarElementType implements ComplexUiElementType {

    BACKGROUND, HEALTH;

    @Override
    public String getName(){

        return this.name();
    }

    @Override
    public Integer getValue() {

        return this.ordinal();
    }
}
