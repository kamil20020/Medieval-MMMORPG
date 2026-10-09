package pl.engine.mmorpg.entity.ui;

import org.joml.Vector2f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.ui.element.complex.health.UiHealthBar;
import pl.engine.mmorpg.render.Window;

public class UiComponent implements Component {

    private UIDrawer uiDrawer;

    public UiComponent(Window window){

        this.uiDrawer = new UIDrawer();

        addUiElements(window);
    }

    private void addUiElements(Window window){

        UiHealthBar healthBar = new UiHealthBar(new Vector2f());
        uiDrawer.addUiElement(healthBar);
    }

    @Override
    public void prepare() {

        uiDrawer.init();
    }

    @Override
    public void update(double deltaTime) {

        uiDrawer.update(deltaTime);
    }

    @Override
    public void draw() {

        uiDrawer.draw();
    }

    @Override
    public void destroy(){

        uiDrawer.clear();
    }
}
