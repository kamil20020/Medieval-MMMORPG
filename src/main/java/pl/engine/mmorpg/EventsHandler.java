package pl.engine.mmorpg;

import org.lwjgl.glfw.GLFW;
import pl.engine.mmorpg.render.Window;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

public class EventsHandler {

    private final Window window;

    private final Set<Integer> pressedKeyboardKeys = new HashSet<>();

    private double mouseX = -1;
    private double mouseXDiff = 0;
    private double mouseY = -1;
    private double mouseYDiff = 0;
    private long lastMouseMoveTime = 0;

    private int eventButtonId = -1;
    private int buttonEventId = -1;

    private static final long MOUSE_IDLE_THRESHOLD = 10;

    public EventsHandler(Window window){

        this.window = window;

        window.setKeyboardCallback(this::handleKeyboard);
        window.setMousePosCallback(this::handleMousePos);
        window.setMouseClickCallback(this::handleMouseClick);

        mouseX = window.getWidth() / 2.0;
        mouseY = window.getHeight() / 2.0;
    }

    private void handleKeyboard(int key, int action){

        if(action == GLFW.GLFW_PRESS){

            pressedKeyboardKeys.add(key);
        }
        else if(action == GLFW.GLFW_RELEASE){

            pressedKeyboardKeys.remove(key);
        }

        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS){

            window.close();
        }
    }

    private void handleMousePos(double x, double y){

        if(mouseX == -1){
            mouseX = x;
            return;
        }

        mouseXDiff = x - mouseX;
        mouseX = x;
        lastMouseMoveTime = System.currentTimeMillis();
    }

    public boolean isMouseIdle(){

        return System.currentTimeMillis() - lastMouseMoveTime > MOUSE_IDLE_THRESHOLD;
    }

    private void handleMouseClick(int button, int action){

       eventButtonId = button;
       buttonEventId = action;
    }

    public boolean isKeyPressed(int keyCode){

        return pressedKeyboardKeys.contains(keyCode);
    }

    public double getMouseXPosForWindowWidth(){

        return mouseXDiff / window.getWidth();
    }

    public double getMouseYForWindowHeight(){

        return mouseY / window.getHeight();
    }

    public int getEventButtonId(){

        return eventButtonId;
    }

    public int getButtonEventId(){

        return buttonEventId;
    }

    public void resetMouseMove(){

        mouseXDiff = 0;
        mouseYDiff = 0;
    }

}
