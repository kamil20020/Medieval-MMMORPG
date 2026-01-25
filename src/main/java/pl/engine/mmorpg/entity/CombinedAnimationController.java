package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class CombinedAnimationController {

    private Entity entity;
    private final Map<String, AnimatedMeshable> animations = new HashMap<>();
    private final Map<String, String> animationsKeysPathsMappings;
    private final MeshAbstractFactory meshFactory;

    protected String actualAnimationName = null;
    protected String oldAnimationName = null;
    protected String nextAnimationName = null;

    protected AnimatedMeshable actualAnimation = null;
    protected AnimatedMeshable nextAnimation = null;

    protected double blendTime = 0;
    protected boolean isBlending = false;

    protected static final double BLEND_DURATION = 0.2;

    public CombinedAnimationController(
        Map<String, String> animationsKeysPathsMappings,
        MeshAbstractFactory meshFactory,
        String firstAnimationName
    ){
        this.animationsKeysPathsMappings = animationsKeysPathsMappings;
        this.meshFactory = meshFactory;

        this.actualAnimationName = firstAnimationName;
        this.oldAnimationName = firstAnimationName;
    }

    public void init(Entity entity){

        this.entity = entity;

        loadAnimations(animationsKeysPathsMappings, meshFactory);

        this.actualAnimation = animations.get(actualAnimationName);
    }

    private void loadAnimations(Map<String, String> animationsKeysPathsMappings, MeshAbstractFactory meshFactory){

        for(Map.Entry<String, String> animationNamePathMapping : animationsKeysPathsMappings.entrySet()){

            String animationKey = animationNamePathMapping.getKey();
            String animationPath = animationNamePathMapping.getValue();

            AnimatedMeshable animation = meshFactory.createComplexAnimatedMesh(entity.getComplexMesh(), animationPath);
            animations.put(animationKey, animation);
        }
    }

    public void update(
        double deltaTimeInSeconds,
        MoveDirectionState moveDirectionState,
        MoveState moveState,
        CombatState combatState
    ){
        String newAnimationName = getActualAnimationName(moveDirectionState, moveState, combatState);

        setAnimation(newAnimationName);
        actualAnimation.update(deltaTimeInSeconds);

//        if(!Objects.equals(actualAnimationName, nextAnimationName)){
//
//            nextAnimation.update(deltaTimeInSeconds);
//        }
    }

    private String getActualAnimationName(
        MoveDirectionState moveDirectionState, MoveState moveState, CombatState combatState
    ){
        if(combatState == CombatState.FIGHTING){
            return getKey(combatState);
        }

        if(moveState == MoveState.JUMP){

            return getKey(moveState);
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

    public void clear(){

        actualAnimation.clear();
    }

    public static String getKey(MoveState moveState){

        return moveState.name();
    }

    public static String getKey(MoveDirectionState moveDirectionState){

        return moveDirectionState.name();
    }

    public static String getKey(CombatState combatState){

        return combatState.name();
    }

    public static String getKey(MoveState moveState, MoveDirectionState moveDirectionState){

        return moveState.name() + "_" + moveDirectionState.name();
    }
}
