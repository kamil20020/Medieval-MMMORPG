package pl.engine.mmorpg.entity.input;

public class InputData {

    public boolean switchSprintPressed;
    public boolean cameraUnlockPressed;
    public boolean gravitySwitchPressed;

    public boolean moveLeft;
    public boolean moveRight;
    public boolean moveFront;
    public boolean moveBack;
    public boolean moveTop;

    public boolean rotateLeft;
    public boolean rotateRight;
    public boolean rotateTop;
    public boolean rotateDown;

    public boolean keyboardRotateTopCamera;
    public boolean keyboardRotateDownCamera;
    public boolean mouseRotateCamera;
    public double mouseXPosForWindowWidth;
    public double mouseYPosForWindowHeight;

    public boolean combatStart;
    public boolean useSkill;

    public void reset() {

        this.switchSprintPressed = false;
        this.cameraUnlockPressed = false;
        this.gravitySwitchPressed = false;

        this.moveLeft = false;
        this.moveRight = false;
        this.moveFront = false;
        this.moveBack = false;
        this.moveTop = false;

        this.rotateLeft = false;
        this.rotateRight = false;
        this.rotateTop = false;
        this.rotateDown = false;

        this.keyboardRotateTopCamera = false;
        this.keyboardRotateDownCamera = false;
        this.mouseRotateCamera = false;
        this.mouseXPosForWindowWidth = 0;
        this.mouseYPosForWindowHeight = 0;

        this.combatStart = false;
        this.useSkill = false;
    }
}
