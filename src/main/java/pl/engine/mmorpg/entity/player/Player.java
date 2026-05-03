package pl.engine.mmorpg.entity.player;

import pl.engine.mmorpg.entity.AnimationInfo;
import pl.engine.mmorpg.entity.Entity;
import pl.engine.mmorpg.entity.gravity.GravityMovementComponent;
import pl.engine.mmorpg.entity.combat.CombatState;
import pl.engine.mmorpg.entity.move.MoveComponent;
import pl.engine.mmorpg.entity.move.MoveDirectionState;
import pl.engine.mmorpg.entity.move.MoveState;
import pl.engine.mmorpg.render.Camera;
import pl.engine.mmorpg.EventsHandler;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static pl.engine.mmorpg.entity.CombinedAnimationController.*;

public class Player extends Entity {

    private final Camera camera;
    private final EventsHandler eventsHandler;
    private static final String MODEL_PATH = "models/warrior.glb";
    private static final String FIRST_ANIMATION_NAME = getKey(MoveState.STANDING);

    public Player(Camera camera, EventsHandler eventsHandler, MeshAbstractFactory meshFactory){
        super(
            MODEL_PATH,
            getAnimationNamesPathsMappings(),
            meshFactory,
            FIRST_ANIMATION_NAME
        );

        super.setInputComponent(new PlayerInputComponent(moveComponent, combatComponent, camera, eventsHandler));

        this.camera = camera;
        this.eventsHandler = eventsHandler;

        updatePositionForCamera();
    }

    private static Map<String, AnimationInfo> getAnimationNamesPathsMappings() {

        Map<String, AnimationInfo> result = new HashMap<>();

        result.put(getKey(MoveState.STANDING), getAnimationInfo("animations/warrior/idle.glb"));
        result.put(getKey(MoveState.JUMP, MoveDirectionState.TOP), getAnimationInfo("animations/warrior/move/jump/jump.glb", 1.8f));
        result.put(getKey(MoveState.JUMP, MoveDirectionState.DOWN), getAnimationInfo("animations/warrior/move/jump/fall.glb"));

        result.put(getKey(MoveState.WALK, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/walk/front.glb"));
        result.put(getKey(MoveState.WALK, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/walk/left.glb"));
        result.put(getKey(MoveState.WALK, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/walk/right.glb"));
        result.put(getKey(MoveState.WALK, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/walk/backward.glb"));

        result.put(getKey(MoveState.RUN, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/run/front.glb"));
        result.put(getKey(MoveState.RUN, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/run/left.glb"));
        result.put(getKey(MoveState.RUN, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/run/right.glb"));
        result.put(getKey(MoveState.RUN, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/run/backward.glb"));

        result.put(getKey(CombatState.FIGHTING), getAnimationInfo("animations/warrior/combat/sword-inplace.glb", 1.5f));

        return result;
    }

    private void updatePositionForCamera() {

        position = camera.getRootPosition();
        mesh.setModel(camera.getMatrixRelativeToCamera());
    }

    @Override
    public void update(double deltaTimeInSeconds){

        super.update(deltaTimeInSeconds);

        camera.move(moveComponent.getVelocity());

        updatePositionForCamera();
    }
}
