package pl.engine.mmorpg.entity.input;

import pl.engine.mmorpg.EventsHandler;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerInputComponent extends InputComponent {

    private final EventsHandler eventsHandler;

    public PlayerInputComponent(EventsHandler eventsHandler){

        this.eventsHandler = eventsHandler;
    }

    @Override
    public void update(double deltaTime){

        inputData.reset();
        handleKeyboard();
        handleMouse();
    }

    private void handleKeyboard(){

        handleMove();
        handleActions();
    }

    private void handleActions(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_V)){

            inputData.switchSprintPressed = true;
            eventsHandler.resetKey(GLFW_KEY_V);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_R)){

            inputData.cameraUnlockPressed = true;
            eventsHandler.resetKey(GLFW_KEY_R);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_G)){

            inputData.gravitySwitchPressed = true;
            eventsHandler.resetKey(GLFW_KEY_G);
        }
    }

    private void handleMove(){

        handleMoveWasd();
        handleMoveVertical();
    }

    private void handleMoveWasd(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_W)){

            inputData.moveFront = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_S)){

            inputData.moveBack = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_A)){

            inputData.moveLeft = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_D)){

            inputData.moveRight = true;
        }
    }

    private void handleMoveVertical(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_UP)){
            inputData.keyboardRotateTopCamera = true;
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_DOWN)){
            inputData.keyboardRotateDownCamera = true;
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE)){
            inputData.moveTop = true;
            eventsHandler.resetKey(GLFW_KEY_SPACE);
        }
    }

    private void handleMouse(){

        handleMouseClick();
        handleMouseRotate();
    }

    private void handleMouseRotate(){

        inputData.mouseRotateCamera = true;
        inputData.mouseXPosForWindowHeight = eventsHandler.getMouseXPosForWindowWidth();
        inputData.mouseYPosForWindowHeight = eventsHandler.getMouseYPosForWindowHeight();

        eventsHandler.resetMouseMove();
    }

    private void handleMouseClick(){

        handleAttack();
    }

    private void handleAttack(){

        int eventButtonId = eventsHandler.getEventButtonId();
        int buttonEventId = eventsHandler.getButtonEventId();

        if(eventButtonId == GLFW_MOUSE_BUTTON_1){

            if(buttonEventId == GLFW_PRESS){

                inputData.combatStart = true;
            }
            else if(buttonEventId == GLFW_RELEASE){

                inputData.combatStart = false;
            }
        }
    }
}
