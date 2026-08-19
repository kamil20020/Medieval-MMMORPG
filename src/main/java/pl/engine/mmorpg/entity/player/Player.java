package pl.engine.mmorpg.entity.player;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.animation.AnimationInfo;
import pl.engine.mmorpg.entity.Entity;
import pl.engine.mmorpg.entity.animation.AnimationComponent;
import pl.engine.mmorpg.entity.combat.CombatComponent;
import pl.engine.mmorpg.entity.gravity.GravityMovementComponent;
import pl.engine.mmorpg.entity.gravity.TerrainCollisionComponent;
import pl.engine.mmorpg.entity.input.ActionsComponent;
import pl.engine.mmorpg.entity.input.InputComponent;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.entity.input.PlayerInputComponent;
import pl.engine.mmorpg.entity.move.MoveDirectionState;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.EventsHandler;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;

import java.util.*;

import static pl.engine.mmorpg.entity.animation.AnimationComponent.*;

public class Player extends Entity {

    private AnimationComponent animationComponent = null;

    private static final String MODEL_PATH = "models/warrior.glb";
    private static final String FIRST_ANIMATION_NAME = getKey(EntityState.STANDING);

    private static final Map<String, AnimationInfo> animationNamesPathsMappings;
    static {
        animationNamesPathsMappings = getAnimationNamesPathsMappings();
    }

    public Player(EventsHandler eventsHandler, MeshAbstractFactory meshFactory){
        super(MODEL_PATH, meshFactory);

        List<Component> components = initComponents(eventsHandler, meshFactory);
        addComponents(components);
    }

    private List<Component> initComponents(EventsHandler eventsHandler, MeshAbstractFactory meshFactory){

        TransformComponent transformComponent = new TransformComponent(mesh);

        InputComponent inputComponent = new PlayerInputComponent(eventsHandler);
        InputData inputData = inputComponent.getInputData();

        MovementComponent movementComponent = new MovementComponent(
            inputData,
            entityStateData,
            transformComponent
        );

        GravityMovementComponent gravityMovementComponent = new GravityMovementComponent(
            movementComponent,
            entityStateData,
            transformComponent::getPosition
        );

        TerrainCollisionComponent terrainCollisionComponent = new TerrainCollisionComponent(
            entityStateData,
            movementComponent,
            transformComponent::getPosition
        );

        CombatComponent combatComponent = new CombatComponent(inputData, entityStateData, movementComponent, transformComponent);

        this.animationComponent = new AnimationComponent(
            mesh,
            animationNamesPathsMappings,
            meshFactory,
            FIRST_ANIMATION_NAME,
            movementComponent,
            entityStateData
        );

        ActionsComponent actionsComponent = new ActionsComponent(inputData, entityStateData);

        CameraComponent cameraComponent = new CameraComponent(transformComponent, movementComponent, inputData);

        return List.of(
            inputComponent,
            combatComponent,
            movementComponent,
            gravityMovementComponent,
            terrainCollisionComponent,
            transformComponent,
            actionsComponent,
            animationComponent,
            cameraComponent
        );
    }

    private static Map<String, AnimationInfo> getAnimationNamesPathsMappings() {

        Map<String, AnimationInfo> result = new HashMap<>();

        result.put(getKey(EntityState.STANDING), getAnimationInfo("animations/warrior/idle.glb"));
        result.put(getKey(EntityState.FALLING), getAnimationInfo("animations/warrior/move/jump/fall.glb"));
        result.put(getKey(true, MoveDirectionState.TOP), getAnimationInfo("animations/warrior/move/jump/jump.glb", 1.2f));

        result.put(getKey(false, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/walk/front.glb"));
        result.put(getKey(false, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/walk/left.glb"));
        result.put(getKey(false, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/walk/right.glb"));
        result.put(getKey(false, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/walk/backward.glb"));

        result.put(getKey(true, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/run/front.glb"));
        result.put(getKey(true, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/run/left.glb"));
        result.put(getKey(true, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/run/right.glb"));
        result.put(getKey(true, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/run/backward.glb"));

        result.put(getKey(EntityState.COMBAT), getAnimationInfo("animations/warrior/combat/sword-inplace.glb", 1.5f));

        return result;
    }

    @Override
    public void draw() {

        animationComponent.draw();
    }

    @Override
    public void update(double deltaTimeInSeconds){

        super.update(deltaTimeInSeconds);
    }
}
