package pl.engine.mmorpg.entity.animation;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.DynamicMesh;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.combat.SkillType;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.entity.move.MoveDirectionState;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class AnimationComponent implements Component {

    private ComplexMesh complexMesh;
    private final MovementComponent movementComponent;
    private final EntityStateData entityStateData;

    private double blockingAnimationStartTime = 0;

    private final Map<String, AnimatedMeshable> animations = new HashMap<>();
    private final Map<String, AnimationInfo> animationsKeysInfoMappings;
    private final MeshAbstractFactory meshFactory;

    protected String actualAnimationName = null;

    protected AnimatedMeshable actualAnimation = null;
    protected AnimatedMeshable nextAnimation = null;

    protected double blendStartTime = 0;
    protected boolean isBlending = false;

    private static final String IS_SPRINTING_KEY = "is_sprinting";
    private static final String IS_WALKING_KEY = "is_walking";

    private static final String IS_HIDDEN_WEAPON_KEY = "is_weapon_hidden";
    private static final String IS_NOT_HIDDEN_WEAPON_KEY = "is_weapon_not_hidden";

    private static final String KEY_SEPARATOR = "-";

    public AnimationComponent(
        ComplexMesh complexMesh,
        Map<String, AnimationInfo> animationsKeysPathsMappings,
        MeshAbstractFactory meshFactory,
        String firstAnimationName,
        MovementComponent movementComponent,
        EntityStateData entityStateData
    ){
        this.complexMesh = complexMesh;

        this.animationsKeysInfoMappings = animationsKeysPathsMappings;
        this.meshFactory = meshFactory;

        this.actualAnimationName = firstAnimationName;

        this.movementComponent = movementComponent;
        this.entityStateData = entityStateData;
    }

    @Override
    public void prepare(){

        init(complexMesh);
        uploadToGpu();
    }

    public void init(ComplexMesh complexMesh){

        this.complexMesh = complexMesh;

        loadAnimations();

        this.actualAnimation = animations.get(actualAnimationName);
    }

    private void loadAnimations(){

        for(Map.Entry<String, AnimationInfo> animationNameInfoMapping : animationsKeysInfoMappings.entrySet()){

            String animationKey = animationNameInfoMapping.getKey();

            AnimationInfo animationInfo = animationNameInfoMapping.getValue();
            String animationPath = animationInfo.path();
            float animationSpeedMultiplier = animationInfo.animationSpeedMultiplier();

            AnimatedMeshable animation = meshFactory.createComplexAnimatedMesh(complexMesh, animationPath, animationSpeedMultiplier);
            animations.put(animationKey, animation);
        }
    }

    public void addDynamicMesh(DynamicMesh dynamicMesh){

        for(AnimatedMeshable animatedMesh : animations.values()){

            animatedMesh.addDynamicMesh(dynamicMesh);
        }
    }

    @Override
    public void update(double deltaTimeInSeconds){

        if(isBlending){

            blendAnimations();
        }

        if(entityStateData.canActionBeInterrupted){

            blockingAnimationStartTime = 0;
            startNewAnimation();
        }
        else{

            handleBlockingAnimation();
        }

        actualAnimation.update(deltaTimeInSeconds);
    }

    private void handleBlockingAnimation(){

        if(blockingAnimationStartTime == 0){

            startNewAnimation();
            blockingAnimationStartTime = System.nanoTime();
            return;
        }

        double animationDuration = getBlockingAnimationDuration();

        if(animationDuration < entityStateData.actionMinimumDuration){
            return;
        }

        entityStateData.canActionBeInterrupted = true;
        entityStateData.actionMinimumDuration = 0;
        blockingAnimationStartTime = 0;
    }

    private double getBlockingAnimationDuration(){

        double actualTime = System.nanoTime();
        return (actualTime - blockingAnimationStartTime) / 1_000_000_000d;
    }

    private void startNewAnimation(){

        String newAnimationName = getActualAnimationName();
        startNewAnimation(newAnimationName);
    }

    private String getActualAnimationName(){

        EntityState entityState = entityStateData.entityState;
        MoveDirectionState moveDirectionState = movementComponent.getMoveDirectionState();
        boolean isSprinting = entityStateData.isSprinting;
        boolean isWeaponHidden = entityStateData.isWeaponHidden;

        if(entityStateData.isInAir && entityState != EntityState.FALLING){

            return getKey(isWeaponHidden, isSprinting, MoveDirectionState.TOP);
        }

        if(entityState == EntityState.MOVE){

            return getKey(isWeaponHidden, isSprinting, moveDirectionState);
        }

        return getKey(isWeaponHidden, entityState);
    }

    private void startNewAnimation(String animationName){

        if(Objects.equals(actualAnimationName, animationName)){
            return;
        }

        if(actualAnimationName != null){

            nextAnimation = animations.get(animationName);
            startAnimationsBlending();
        }
        else{
            actualAnimation.reset();
            actualAnimation = animations.get(animationName);
        }

        actualAnimationName = animationName;
    }

    public void setBlockingAnimation(String animationName){

        if(animationName == null){

            startNewAnimation();
            return;
        }

        blockingAnimationStartTime = System.nanoTime();

        startNewAnimation(animationName);
    }

    private void startAnimationsBlending(){

        isBlending = true;
        blendStartTime = glfwGetTime();
        actualAnimation.setNextAnimation(nextAnimation);
    }

    private void blendAnimations(){

        double actualTime = glfwGetTime();
        double blendingTime = actualTime - blendStartTime;

        float blendProgress = (float) Math.min(blendingTime / AnimatedMesh.BLENDING_DURATION, 1.0);

        if(blendProgress < 1){
            actualAnimation.setBlendingProgress(blendProgress);
            return;
        }

        blendStartTime = 0;
        isBlending = false;
        actualAnimation.reset();
        actualAnimation = nextAnimation;
    }

    public void uploadToGpu(){

        for(AnimatedMeshable animatedMeshable : animations.values()){
            animatedMeshable.uploadToGpu();
        }
    }

    @Override
    public void draw(){

        actualAnimation.draw();
    }

    @Override
    public void clear(){

        actualAnimation.clear();
    }

    public static AnimationInfo getAnimationInfo(String animationModelInfo){

        return new AnimationInfo(animationModelInfo);
    }

    public static AnimationInfo getAnimationInfo(String animationModelInfo, float animationSpeedMultiplier){

        return new AnimationInfo(animationModelInfo, animationSpeedMultiplier);
    }

    public static String getKey(boolean isWeaponHidden, EntityState entityState){

        String isWeaponHiddenKey = isWeaponHidden ? IS_HIDDEN_WEAPON_KEY : IS_NOT_HIDDEN_WEAPON_KEY;

        return isWeaponHiddenKey + KEY_SEPARATOR + entityState.name();
    }

    public static String getKey(boolean isWeaponHidden, boolean isSprinting, MoveDirectionState moveDirectionState){

        String isWeaponHiddenKey = isWeaponHidden ? IS_HIDDEN_WEAPON_KEY : IS_NOT_HIDDEN_WEAPON_KEY;
        String moveTypeKey = isSprinting ? IS_SPRINTING_KEY : IS_WALKING_KEY;

        return isWeaponHiddenKey + KEY_SEPARATOR + moveTypeKey + KEY_SEPARATOR + moveDirectionState.name();
    }

    public static String getKey(SkillType skillType){

        return skillType.name();
    }
}
