package pl.engine.mmorpg.entity.ui;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.render.Window;

public class UiComponent implements Component {

    private UIDrawer uiDrawer;

    private final Window window;

    public UiComponent(Window window){

        this.window = window;
        this.uiDrawer = new UIDrawer(window);
    }

    @Override
    public void prepare() {

        uiDrawer.init();
    }

    @Override
    public void update(double deltaTime) {


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
