package pl.engine.mmorpg.entity.player;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.render.Camera;

public class CameraComponent implements Component {

    private final Camera camera;
    private boolean isVerticalCameraUnlocked = false;
    private final TransformComponent transformComponent;
    private final MovementComponent movementComponent;
    private InputData inputData;

    protected static final double ROTATION_SENS = 50000;

    public CameraComponent(TransformComponent transformComponent, MovementComponent movementComponent, InputData inputData){

        this.transformComponent = transformComponent;
        this.camera = new Camera(transformComponent.getPosition().add(Camera.CAMERA_OFFSET));
        this.movementComponent = movementComponent;
        this.inputData = inputData;
    }

    @Override
    public void update(double deltaTimeInSeconds){

        camera.update();

        Vector3f velocity = movementComponent.getVelocity();
        camera.move(velocity);

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

        handleHorizontalRotate(deltaTimeInSeconds, inputComponent.mouseXPosForWindowWidth);

        if(isVerticalCameraUnlocked){

            handleVerticalRotate(deltaTimeInSeconds, inputComponent.mouseYPosForWindowHeight);
        }
    }

    private void handleHorizontalRotate(double deltaTimeInSeconds, double mouseX){

        if(!inputData.rotateLeft && !inputData.rotateRight){
            return;
        }

        double rotationValue = Math.abs(mouseX) * ROTATION_SENS * deltaTimeInSeconds;

        if(inputData.rotateRight){

            camera.rotateRight(rotationValue);
        }
        else{

            camera.rotateLeft(rotationValue);
        }
    }

    private void handleVerticalRotate(double deltaTimeInSeconds, double mouseY){

        if(!inputData.rotateTop && !inputData.rotateDown){
            return;
        }

        double rotationValue = Math.abs(mouseY) * ROTATION_SENS * deltaTimeInSeconds;

        if(inputData.rotateDown){

            camera.rotateDown(rotationValue);
        }
        else{

            camera.rotateTop(rotationValue);
        }
    }
}
