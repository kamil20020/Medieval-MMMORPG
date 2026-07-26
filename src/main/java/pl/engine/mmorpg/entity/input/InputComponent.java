package pl.engine.mmorpg.entity.input;

public class InputComponent {

    public boolean switchSprintPressed;
    public boolean cameraUnlockPressed;
    public boolean gravitySwitchPressed;

    public boolean moveLeft;
    public boolean moveRight;
    public boolean moveFront;
    public boolean moveBack;
    public boolean moveTop;

    public boolean keyboardRotateTopCamera;
    public boolean keyboardRotateDownCamera;
    public boolean mouseRotateCamera;
    public double mouseXPosForWindowHeight;
    public double mouseYPosForWindowHeight;

    public boolean combatStart;

    public void reset() {

        this.switchSprintPressed = false;
        this.cameraUnlockPressed = false;
        this.gravitySwitchPressed = false;

        this.moveLeft = false;
        this.moveRight = false;
        this.moveFront = false;
        this.moveBack = false;
        this.moveTop = false;

        this.keyboardRotateTopCamera = false;
        this.keyboardRotateDownCamera = false;
        this.mouseRotateCamera = false;
        this.mouseXPosForWindowHeight = 0;
        this.mouseYPosForWindowHeight = 0;

        this.combatStart = false;
    }
}
