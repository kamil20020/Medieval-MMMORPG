package pl.engine.mmorpg.render;

import org.lwjgl.glfw.GLFWVidMode;
import pl.engine.mmorpg.shaders.Shader;
import org.lwjgl.opengl.GL;

import java.util.function.BiConsumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final long windowId;
    private int width;
    private int height;

    public Window(int width, int height){

        if(!glfwInit()){
            throw new IllegalStateException("Could now init glfw");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Pobranie trybu wideo monitora (rozdzielczość)
        long primaryMonitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = glfwGetVideoMode(primaryMonitor);

        this.width = vidMode.width();
        this.height = vidMode.height();

//        windowId = glfwCreateWindow(width, height, "Engine 3d - OpenGL from lwjgl", NULL, NULL);
        windowId = glfwCreateWindow(this.width, this.height, "Engine 3d - OpenGL from lwjgl", primaryMonitor, 0);

        if(windowId == NULL){

            glfwTerminate();

            throw new IllegalStateException("Could not create glfw window");
        }

        glfwMakeContextCurrent(windowId);
        glfwSetFramebufferSizeCallback(windowId, (windowId, newWidth, newHeight) -> updateViewPort(this.width, this.height));
        glfwSwapInterval(1); // vertical sync
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetInputMode(windowId, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
    }

    public void start(){

        glfwShowWindow(windowId);

        GL.createCapabilities(); // collaboration between lwjgl, opengl and glfw

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);
        glClearColor(0f, 0f, 0f, 1.0f);

        Shader shader = Shader.getInstance("shaders/vertex.vert", "shaders/fragment.frag");
        shader.useShader();

        updateViewPort(width, height);
    }

    public void stop(){

        glfwTerminate();
    }

    public void close(){

        glfwSetWindowShouldClose(windowId, true);
    }

    public void clearScreen(){

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void refreshScreen(){

        glFlush();

        glfwSwapBuffers(windowId);
    }

    public void handleEvents(){

        glfwPollEvents();
    }

    public boolean isWindowClosed(){

        return glfwWindowShouldClose(windowId);
    }

    public void setKeyboardCallback(BiConsumer<Integer, Integer> callback){

        glfwSetKeyCallback(windowId, (windowId, key, scancode, action, mode) -> callback.accept(key, action));
    }

    public void setMousePosCallback(BiConsumer<Double, Double> callback){

        glfwSetCursorPosCallback(windowId, (windowId, x, y) -> callback.accept(x, y));
    }

    public void setMouseClickCallback(BiConsumer<Integer, Integer> callback){

        glfwSetMouseButtonCallback(windowId, (windowId, button, action, mode) -> callback.accept(button, action));
    }

    private void updateViewPort(int newWidth, int newHeight){

        this.width = newWidth;
        this.height = newHeight;

        Perspective.init(width, height);

        glViewport(0, 0, width, height);
    }

    public int getWidth(){

        return width;
    }

    public int getHeight(){

        return height;
    }
}
