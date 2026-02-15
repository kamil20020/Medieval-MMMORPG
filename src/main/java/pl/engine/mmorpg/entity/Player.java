package pl.engine.mmorpg.entity;

import pl.engine.mmorpg.render.Camera;
import pl.engine.mmorpg.EventsHandler;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static pl.engine.mmorpg.entity.CombinedAnimationController.*;

public class Player extends Entity {

    private Vector3f position;

    private final Camera camera;
    private final EventsHandler eventsHandler;

    private final MoveComponent moveComponent;
    private final PlayerInputComponent playerInputComponent;

    private static final String MODEL_PATH = "models/warrior.glb";
    private static final String FIRST_ANIMATION_NAME = getKey(MoveState.STANDING);

    public Player(Camera camera, EventsHandler eventsHandler, MeshAbstractFactory meshFactory){
        super(MODEL_PATH, getAnimationNamesPathsMappings(), meshFactory, FIRST_ANIMATION_NAME);

        this.camera = camera;
        this.eventsHandler = eventsHandler;
        this.moveComponent = new MoveComponent(this);
        this.playerInputComponent = new PlayerInputComponent(this, camera, eventsHandler);

        updatePositionForCamera();
    }

    private static Map<String, String> getAnimationNamesPathsMappings() {

        Map<String, String> result = new HashMap<>();

        result.put(getKey(MoveState.STANDING), "animations/warrior/idle.glb");
        result.put(getKey(MoveState.JUMP), "animations/warrior/move/jump.glb");

        result.put(getKey(MoveState.WALK, MoveDirectionState.FRONT), "animations/warrior/move/walk/front.glb");
        result.put(getKey(MoveState.WALK, MoveDirectionState.LEFT), "animations/warrior/move/walk/left.glb");
        result.put(getKey(MoveState.WALK, MoveDirectionState.RIGHT), "animations/warrior/move/walk/right.glb");
        result.put(getKey(MoveState.WALK, MoveDirectionState.BACK), "animations/warrior/move/walk/backward.glb");

        result.put(getKey(MoveState.RUN, MoveDirectionState.FRONT), "animations/warrior/move/run/front.glb");
        result.put(getKey(MoveState.RUN, MoveDirectionState.LEFT), "animations/warrior/move/run/left.glb");
        result.put(getKey(MoveState.RUN, MoveDirectionState.RIGHT), "animations/warrior/move/run/right.glb");
        result.put(getKey(MoveState.RUN, MoveDirectionState.BACK), "animations/warrior/move/run/backward.glb");

        result.put(getKey(CombatState.FIGHTING), "animations/warrior/combat/sword-inplace.glb");

        return result;
    }

    private void updatePositionForCamera() {

        position = camera.getRootPosition();
        mesh.setModel(camera.getMatrixRelativeToCamera());
    }

    private void handleAttack(){

        int eventButtonId = eventsHandler.getEventButtonId();
        int buttonEventId = eventsHandler.getButtonEventId();

        if(eventButtonId == GLFW_MOUSE_BUTTON_1){

            if(buttonEventId == GLFW_PRESS){

                moveState = MoveState.STANDING;
                combatState = CombatState.FIGHTING;
            }
            else if(buttonEventId == GLFW_RELEASE){

                combatState = CombatState.NO_WEAPON;
            }
        }
    }

    public MoveComponent getPlayerMoveComponent(){

        return moveComponent;
    }

    public void move(Vector3f vec){

        position.x += vec.x;
        position.x += vec.x;
        position.x += vec.x;
    }

    public Vector3f getPosition(){

        return position;
    }

    @Override
    public void update(double deltaTimeInSeconds){

        moveComponent.resetMove();
        combatState = CombatState.NO_WEAPON;

        playerInputComponent.update(deltaTimeInSeconds);
        handleAttack();

        updatePositionForCamera();

        super.update(deltaTimeInSeconds);
    }
}
