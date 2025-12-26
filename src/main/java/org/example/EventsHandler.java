package org.example;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EventsHandler {

    private final Window window;
    private final List<Consumer<Set<Integer>>> keyboardCallbacks = new ArrayList<>();
    private final Set<Integer> pressedKeyboardKeys = new HashSet<>();

    public EventsHandler(Window window){

        this.window = window;

        window.setKeyboardCallback(this::handleKeyboard);
        window.setMousePosCallback(this::handleMousePos);
        window.setMouseClickCallback(this::handleMouseClick);
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


    }

    public void handleMouseClick(int button, int action){


    }

}
