package org.example;

public class Game {

    private final Window window;
    private final Renderer renderer;
    private EventsHandler eventsHandler;

    public Game(){

        window = new Window(1200, 1200);
        eventsHandler = new EventsHandler(window);
        renderer = new Renderer(window, eventsHandler);
    }

    public void loop(){

        window.start();

        renderer.init();

        while(!window.isWindowClosed()){

            window.clearScreen();

            renderer.render();

            window.refreshScreen();
            window.handleEvents();
        }

        window.stop();
    }
}
