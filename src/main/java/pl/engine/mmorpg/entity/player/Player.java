package pl.engine.mmorpg.entity.player;

import org.joml.Vector3f;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.animation.AnimationInfo;
import pl.engine.mmorpg.entity.Entity;
import pl.engine.mmorpg.entity.animation.AnimationComponent;
import pl.engine.mmorpg.entity.combat.ComboComponent;
import pl.engine.mmorpg.entity.combat.Skill;
import pl.engine.mmorpg.animation.DynamicMesh;
import pl.engine.mmorpg.entity.combat.WeaponComponent;
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
    private final MeshAbstractFactory meshFactory;

    private static final String MODEL_PATH = "models/entities/warrior.glb";
    private static final String FIRST_ANIMATION_NAME = getKey(true, EntityState.STANDING);

    private static final Map<String, AnimationInfo> animationNamesPathsMappings;
    private static final Map<String, Skill> skillsNamesMappings;
    static {
        animationNamesPathsMappings = getAnimationNamesPathsMappings();
        skillsNamesMappings = getSkillsNamesMappings();
    }

    public Player(EventsHandler eventsHandler, MeshAbstractFactory meshFactory){
        super(MODEL_PATH, meshFactory);

        this.meshFactory = meshFactory;

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

        ComboComponent comboComponent = new ComboComponent(inputData, entityStateData, movementComponent, transformComponent);

        this.animationComponent = new AnimationComponent(
            mesh,
            animationNamesPathsMappings,
            meshFactory,
            FIRST_ANIMATION_NAME,
            movementComponent,
            entityStateData
        );

        WeaponComponent weaponComponent = new WeaponComponent(meshFactory, animationComponent, entityStateData, inputData);

        ActionsComponent actionsComponent = new ActionsComponent(inputData, entityStateData);

        CameraComponent cameraComponent = new CameraComponent(transformComponent, movementComponent, inputData);

        return List.of(
            inputComponent,
            comboComponent,
            movementComponent,
            gravityMovementComponent,
            terrainCollisionComponent,
            transformComponent,
            actionsComponent,
            animationComponent,
            weaponComponent,
            cameraComponent
        );
    }

    private static Map<String, AnimationInfo> getAnimationNamesPathsMappings() {

        Map<String, AnimationInfo> result = new HashMap<>();

        putAnimationsForHiddenWeapon(result);
        putAnimationsForNotHiddenWeapon(result);

        return result;
    }

    private static void putAnimationsForHiddenWeapon(Map<String, AnimationInfo> result){

        result.put(getKey(true, EntityState.STANDING), getAnimationInfo("animations/warrior/idle.glb"));
        result.put(getKey(true, EntityState.FALLING), getAnimationInfo("animations/warrior/move/jump/fall.glb"));
        result.put(getKey(true, EntityState.EQUIP_WEAPON), getAnimationInfo("animations/warrior/combat/sword/hide.glb"));

        result.put(getKey(true,false, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/walk/front.glb"));
        result.put(getKey(true,false, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/walk/left.glb"));
        result.put(getKey(true,false, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/walk/right.glb"));
        result.put(getKey(true,false, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/walk/backward.glb"));

        result.put(getKey(true,true, MoveDirectionState.TOP), getAnimationInfo("animations/warrior/move/jump/jump.glb", 1.2f));
        result.put(getKey(true,true, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/run/front.glb"));
        result.put(getKey(true,true, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/run/left.glb"));
        result.put(getKey(true,true, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/run/right.glb"));
        result.put(getKey(true,true, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/run/backward.glb"));
    }

    private static void putAnimationsForNotHiddenWeapon(Map<String, AnimationInfo> result){

        result.put(getKey(false, EntityState.STANDING), getAnimationInfo("animations/warrior/idle.glb"));
        result.put(getKey(false, EntityState.FALLING), getAnimationInfo("animations/warrior/move/jump/fall.glb"));
        result.put(getKey(false, EntityState.EQUIP_WEAPON), getAnimationInfo("animations/warrior/combat/sword/show.glb"));
        result.put(getKey(false, EntityState.COMBAT), getAnimationInfo("animations/warrior/combat/sword/combo.glb", 1.5f));

        result.put(getKey(false,false, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/move/walk/front.glb"));
        result.put(getKey(false,false, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/walk/left.glb"));
        result.put(getKey(false,false, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/walk/right.glb"));
        result.put(getKey(false,false, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/walk/backward.glb"));

        result.put(getKey(false,true, MoveDirectionState.TOP), getAnimationInfo("animations/warrior/move/jump/jump.glb", 1.2f));
        result.put(getKey(false,true, MoveDirectionState.FRONT), getAnimationInfo("animations/warrior/combat/sword/run/front.glb"));
        result.put(getKey(false,true, MoveDirectionState.LEFT), getAnimationInfo("animations/warrior/move/run/left.glb"));
        result.put(getKey(false,true, MoveDirectionState.RIGHT), getAnimationInfo("animations/warrior/move/run/right.glb"));
        result.put(getKey(false,true, MoveDirectionState.BACK), getAnimationInfo("animations/warrior/move/run/backward.glb"));
    }

    private static Map<String, Skill> getSkillsNamesMappings(){

        Map<String, Skill> results = new HashMap<>();

        return results;
    }

    @Override
    public void uploadToGpu() {

        super.uploadToGpu();
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
