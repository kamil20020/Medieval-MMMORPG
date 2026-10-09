package pl.engine.mmorpg.entity.ui.element.complex.health;

import org.joml.Vector2f;
import pl.engine.mmorpg.entity.ui.element.SimpleUiElement;
import pl.engine.mmorpg.entity.ui.element.UiElement;
import pl.engine.mmorpg.entity.ui.element.complex.ComplexUiElement;
import pl.engine.mmorpg.entity.ui.element.complex.ComplexUiElementType;

import java.util.LinkedHashMap;
import java.util.Map;

public class UiHealthBar extends ComplexUiElement {

    public UiHealthBar(Vector2f bottomLeftCorner) {
        super(bottomLeftCorner);
    }

    @Override
    protected Map<ComplexUiElementType, UiElement> appendUiElements() {

        Map<ComplexUiElementType, UiElement> uiElements = new LinkedHashMap<>();

        UiElement healthBackground = new SimpleUiElement(bottomLeftCorner, 20, 500, 0f,0f);
        uiElements.put(UiHealthBarElementType.BACKGROUND, healthBackground);

        UiElement healthBar = new SimpleUiElement(bottomLeftCorner, 500, 20, 0f, 0f);
        uiElements.put(UiHealthBarElementType.HEALTH, healthBar);

        return uiElements;
    }

    @Override
    public void update(double deltaTime) {

//        System.out.println("Health");
    }
}
