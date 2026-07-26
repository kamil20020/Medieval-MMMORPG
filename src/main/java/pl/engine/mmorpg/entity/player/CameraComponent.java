package pl.engine.mmorpg.entity.player;

import pl.engine.mmorpg.entity.input.InputComponent;
import pl.engine.mmorpg.render.Camera;

public class CameraComponent {

    private final Camera camera;
    private boolean isVerticalCameraUnlocked = false;

    protected static final double ROTATION_SENS = 50000;

    public CameraComponent(Camera camera){

        this.camera = camera;
    }

    public void update(InputComponent inputComponent, double deltaTimeInSeconds){

        if(inputComponent.cameraUnlockPressed){
            isVerticalCameraUnlocked = !isVerticalCameraUnlocked;
        }
        else if(inputComponent.mouseRotateCamera){
            handleMouseRotate(deltaTimeInSeconds, inputComponent);
        }
        else if(inputComponent.keyboardRotateTopCamera){
            camera.rotateTop(deltaTimeInSeconds);
        }
        else if(inputComponent.keyboardRotateDownCamera){
            camera.rotateDown(deltaTimeInSeconds);
        }
    }

    private void handleMouseRotate(double deltaTimeInSeconds, InputComponent inputComponent){

        handleHorizontalRotate(deltaTimeInSeconds, inputComponent.mouseXPosForWindowHeight);

        if(isVerticalCameraUnlocked){

            handleVerticalRotate(deltaTimeInSeconds, inputComponent.mouseYPosForWindowHeight);
        }
    }

    private void handleHorizontalRotate(double deltaTimeInSeconds, double mouseX){

        if(mouseX == 0){
            return;
        }

        double rotationValue = Math.abs(mouseX) * ROTATION_SENS * deltaTimeInSeconds;

        if(mouseX > 0){

            camera.rotateRight(rotationValue);
        }
        else{

            camera.rotateLeft(rotationValue);
        }
    }

    private void handleVerticalRotate(double deltaTimeInSeconds, double mouseY){

        if(mouseY == 0){
            return;
        }

        double rotationValue = Math.abs(mouseY) * ROTATION_SENS * deltaTimeInSeconds;

        if(mouseY > 0){

            camera.rotateDown(rotationValue);
        }
        else{

            camera.rotateTop(rotationValue);
        }
    }
}
