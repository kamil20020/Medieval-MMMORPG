package org.example;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Game {

    private final Window window;
    private final Renderer renderer;
    private EventsHandler eventsHandler;
    private double lastFrameTime;

    public Game(){

        window = new Window(1200, 1200);
        eventsHandler = new EventsHandler(window);
        renderer = new Renderer(window, eventsHandler);
    }

    public void loop(){

        window.start();

        renderer.init(window.getShaderId());

        while(!window.isWindowClosed()){

            double currentFrameTime = glfwGetTime();
            double deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            window.clearScreen();

            renderer.render(deltaTime);

            window.refreshScreen();
            window.handleEvents();
        }

        renderer.clear();

        window.stop();
    }
}
