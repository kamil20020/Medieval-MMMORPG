package pl.engine.mmorpg.entity.ui;

import org.joml.Vector2f;
import pl.engine.mmorpg.render.Window;

public class UiHealthBar extends UIElement{

    public UiHealthBar(Window window) {

        super(new Vector2f(0, 0), window.getWidth(), 50, 0, 0);
    }

    @Override
    public void update(double deltaTime) {

        System.out.println("Health");
    }
}
