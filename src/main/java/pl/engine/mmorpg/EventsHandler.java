package pl.engine.mmorpg;

import org.lwjgl.glfw.GLFW;
import pl.engine.mmorpg.render.Window;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventsHandler {

    private final Window window;
    private final List<Consumer<Set<Integer>>> keyboardCallbacks = new ArrayList<>();
    private final List<BiConsumer<Double, Double>> mousePosCallbacks = new ArrayList<>();
    private final List<BiConsumer<Integer, Integer>> mouseClickCallbacks = new ArrayList<>();
    private final Set<Integer> pressedKeyboardKeys = new HashSet<>();
    private double mouseX = -1;
    private double mouseXDiff = 0;
    private long lastMouseMoveTime = 0;
    private static final long MOUSE_IDLE_THRESHOLD = 10;

    public EventsHandler(Window window){

        this.window = window;

        window.setKeyboardCallback(this::handleKeyboard);
        window.setMousePosCallback(this::handleMousePos);
        window.setMouseClickCallback(this::handleMouseClick);
        mouseX = window.getWidth() / 2.0;
    }

    public void addKeyboardCallback(Consumer<Set<Integer>> callback){

        keyboardCallbacks.add(callback);
    }

    public void handleKeyboard(int key, int action){

        if(action == GLFW.GLFW_PRESS){

            pressedKeyboardKeys.add(key);
        }
        else if(action == GLFW.GLFW_RELEASE){

            pressedKeyboardKeys.remove(key);
        }

        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS){

            window.close();
        }

        keyboardCallbacks
            .forEach(callback -> callback.accept(pressedKeyboardKeys));
    }

    public void handleMousePos(double x, double y){

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

    public void addMouseClickCallback(BiConsumer<Integer, Integer> callback){

        mouseClickCallbacks.add(callback);
    }

    public void handleMouseClick(int button, int action){

        mouseClickCallbacks
            .forEach(callback -> callback.accept(button, action));
    }

    public boolean isKeyPressed(int keyCode){

        return pressedKeyboardKeys.contains(keyCode);
    }

    public double getMouseXScaleForWindowWidth(){

        return mouseXDiff / window.getWidth();
    }

    public double getMouseYScaleForWindowHeight(double mouseY){

        double windowHeight = window.getHeight();

        return mouseY / windowHeight;
    }

}
