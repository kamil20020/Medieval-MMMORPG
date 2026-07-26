package pl.engine.mmorpg.entity.player;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.render.Camera;

public class CameraComponent implements Component {

    private final Camera camera;
    private boolean isVerticalCameraUnlocked = false;
    private InputData inputData;

    protected static final double ROTATION_SENS = 50000;

    public CameraComponent(Camera camera, InputData inputData){

        this.camera = camera;
        this.inputData = inputData;
    }

    @Override
    public void update(double deltaTimeInSeconds){

        if(inputData.cameraUnlockPressed){
            isVerticalCameraUnlocked = !isVerticalCameraUnlocked;
        }
        else if(inputData.mouseRotateCamera){
            handleMouseRotate(deltaTimeInSeconds, inputData);
        }
        else if(inputData.keyboardRotateTopCamera){
            camera.rotateTop(deltaTimeInSeconds);
        }
        else if(inputData.keyboardRotateDownCamera){
            camera.rotateDown(deltaTimeInSeconds);
        }
    }

    private void handleMouseRotate(double deltaTimeInSeconds, InputData inputComponent){

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
