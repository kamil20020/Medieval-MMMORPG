package pl.engine.mmorpg.entity.input;

import pl.engine.mmorpg.EventsHandler;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerInputController extends InputController{

    private final EventsHandler eventsHandler;

    public PlayerInputController(EventsHandler eventsHandler){

        this.eventsHandler = eventsHandler;
    }

    @Override
    public void update(){

        inputComponent.reset();
        handleKeyboard();
        handleMouse();
    }

    private void handleKeyboard(){

        handleMove();
        handleActions();
    }

    private void handleActions(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_V)){

            inputComponent.switchSprintPressed = true;
            eventsHandler.resetKey(GLFW_KEY_V);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_R)){

            inputComponent.cameraUnlockPressed = true;
            eventsHandler.resetKey(GLFW_KEY_R);
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_G)){

            inputComponent.gravitySwitchPressed = true;
            eventsHandler.resetKey(GLFW_KEY_G);
        }
    }

    private void handleMove(){

        handleMoveWasd();
        handleMoveVertical();
    }

    private void handleMoveWasd(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_W)){

            inputComponent.moveFront = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_S)){

            inputComponent.moveBack = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_A)){

            inputComponent.moveLeft = true;
        }
        else if(eventsHandler.isKeyPressed(GLFW_KEY_D)){

            inputComponent.moveRight = true;
        }
    }

    private void handleMoveVertical(){

        if(eventsHandler.isKeyPressed(GLFW_KEY_UP)){
            inputComponent.keyboardRotateTopCamera = true;
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_DOWN)){
            inputComponent.keyboardRotateDownCamera = true;
        }

        if(eventsHandler.isKeyPressed(GLFW_KEY_SPACE)){
            inputComponent.moveTop = true;
            eventsHandler.resetKey(GLFW_KEY_SPACE);
        }
    }

    private void handleMouse(){

        handleMouseClick();
        handleMouseRotate();
    }

    private void handleMouseRotate(){

        inputComponent.mouseRotateCamera = true;
        inputComponent.mouseXPosForWindowHeight = eventsHandler.getMouseXPosForWindowWidth();
        inputComponent.mouseYPosForWindowHeight = eventsHandler.getMouseYPosForWindowHeight();

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

                inputComponent.combatStart = true;
            }
            else if(buttonEventId == GLFW_RELEASE){

                inputComponent.combatStart = false;
            }
        }
    }
}
