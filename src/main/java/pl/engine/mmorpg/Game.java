package pl.engine.mmorpg;

import pl.engine.mmorpg.render.Renderer;
import pl.engine.mmorpg.render.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Game {

    private final Window window;
    private final Renderer renderer;
    private EventsHandler eventsHandler;
    private double lastFrameTime;

    public Game(){

        window = new Window(1400, 1200);
        eventsHandler = new EventsHandler(window);
        renderer = new Renderer(window, eventsHandler);
    }

    private void loop(){

        window.start();

        renderer.init();

        while(!window.isWindowClosed()){

            double currentFrameTime = glfwGetTime();
            double deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            window.handleEvents();

            window.clearScreen();

            renderer.render(deltaTime);

            window.refreshScreen();
        }

        renderer.clear();

        window.stop();
    }

    public void run(){

        loop();
    }

    public static void main(String[] args){

        System.out.println("Hello World! :D");

        new Game().run();
    }
}
