package pl.engine.mmorpg.entity.animation;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.move.MovementComponent;
import pl.engine.mmorpg.entity.move.MoveDirectionState;
import pl.engine.mmorpg.entity.move.MoveState;
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
    private final Supplier<EntityState> getEntityState;

    private final Map<String, AnimatedMeshable> animations = new HashMap<>();
    private final Map<String, AnimationInfo> animationsKeysInfoMappings;
    private final MeshAbstractFactory meshFactory;

    protected String actualAnimationName = null;
    protected String oldAnimationName = null;
    protected String nextAnimationName = null;

    protected AnimatedMeshable actualAnimation = null;
    protected AnimatedMeshable nextAnimation = null;

    protected double blendTime = 0;
    protected boolean isBlending = false;

    protected static final double BLEND_DURATION = 0.2;

    public AnimationComponent(
        ComplexMesh complexMesh,
        Map<String, AnimationInfo> animationsKeysPathsMappings,
        MeshAbstractFactory meshFactory,
        String firstAnimationName,
        MovementComponent movementComponent,
        Supplier<EntityState> getEntityState
    ){
        this.complexMesh = complexMesh;

        this.animationsKeysInfoMappings = animationsKeysPathsMappings;
        this.meshFactory = meshFactory;

        this.actualAnimationName = firstAnimationName;
        this.oldAnimationName = firstAnimationName;

        this.movementComponent = movementComponent;
        this.getEntityState = getEntityState;
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

    @Override
    public void update(double deltaTimeInSeconds){
        EntityState entityState = getEntityState.get();
        String newAnimationName = getActualAnimationName();

        setAnimation(newAnimationName);
        actualAnimation.update(deltaTimeInSeconds);

//        if(!Objects.equals(actualAnimationName, nextAnimationName)){
//
//            nextAnimation.update(deltaTimeInSeconds);
//        }
    }

    private String getActualAnimationName(){

        EntityState entityState = getEntityState.get();
        MoveState moveState = movementComponent.getMoveState();
        MoveDirectionState moveDirectionState = movementComponent.getMoveDirectionState();

        if(entityState == EntityState.COMBAT){
            return getKey(entityState);
        }

        if(moveState == MoveState.JUMP){

            return getKey(moveState, moveDirectionState);
        }

        if(moveState != MoveState.STANDING){

            return getKey(moveState, moveDirectionState);
        }

        return getKey(moveState);
    }

    public void setAnimation(String animationName){

//        blendAnimationsLogic();

        if(Objects.equals(oldAnimationName, animationName)){
            return;
        }

        oldAnimationName = animationName;
        actualAnimationName = animationName;

        actualAnimation.reset();
        actualAnimation = animations.get(animationName);
    }

    private void blendAnimationsLogic(){

        if(Objects.equals(actualAnimationName, nextAnimationName)) {
            return;
        }

        if(!isBlending){

            isBlending = true;
            blendTime = glfwGetTime();

            return;
        }

        double time = glfwGetTime();
        double diff = time - blendTime;

        float t = (float) Math.min(diff / BLEND_DURATION, 1.0);
        blendAnimations(t);

        if(t >= 1.0){

            isBlending = false;
            actualAnimationName = nextAnimationName;
            blendTime = 0;
        }
    }

    private void blendAnimations(float t){

        List<Matrix4f[]> actualFinals = actualAnimation.getFinalBones();
        List<Matrix4f[]> nextFinals = nextAnimation.getFinalBones();

        for(int i = 0; i < actualFinals.size(); i++){

            Matrix4f[] actualFinal = actualFinals.get(i);
            Matrix4f[] nextFinal = nextFinals.get(i);
            Matrix4f[] finals = new Matrix4f[actualFinal.length];

            for(int j = 0; j < actualFinal.length; j++){

                finals[j] = actualFinal[j].lerp(nextFinal[j], t);
            }

            AnimatedMesh actual = actualAnimation.getAnimatedMesh(i);
            actual.setFinals(finals);
        }
    }

    public void uploadToGpu(){

        for(AnimatedMeshable animatedMeshable : animations.values()){
            animatedMeshable.uploadToGpu();
        }
    }

    public void draw(){

        actualAnimation.draw();
    }

    @Override
    public void clear(){

        actualAnimation.clear();
    }

    public static String getKey(MoveState moveState){

        return moveState.name();
    }

    public static AnimationInfo getAnimationInfo(String animationModelInfo){

        return new AnimationInfo(animationModelInfo);
    }

    public static AnimationInfo getAnimationInfo(String animationModelInfo, float animationSpeedMultiplier){

        return new AnimationInfo(animationModelInfo, animationSpeedMultiplier);
    }

    public static String getKey(MoveDirectionState moveDirectionState){

        return moveDirectionState.name();
    }

    public static String getKey(EntityState entityState){

        return entityState.name();
    }

    public static String getKey(MoveState moveState, MoveDirectionState moveDirectionState){

        return moveState.name() + "_" + moveDirectionState.name();
    }
}
