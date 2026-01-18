package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import pl.engine.mmorpg.animation.AnimatedMesh;
import pl.engine.mmorpg.animation.AnimatedMeshable;
import pl.engine.mmorpg.animation.Skeleton;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.Mesh;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

import java.util.*;

public class Entity implements Meshable {

    protected ComplexMesh mesh;
    protected Skeleton skeleton;
    protected String actualAnimationName = null;
    protected String oldAnimationName = null;
    protected String nextAnimationName = null;
    protected AnimatedMeshable actualAnimation = null;
    protected AnimatedMeshable nextAnimation = null;
    protected Map<String, AnimatedMeshable> animations = new HashMap<>();
    protected double deltaTimeInSeconds = 0;
    protected static final double BLEND_DURATION = 0.2;
    protected double blendTime = 0;
    protected boolean isBlending = false;

    protected static final double ROTATION_SENS = 800;
    protected static final double MOVE_SENS = 2;

    public Entity(String modelPath, Map<String, String> animationsKeysPathsMappings, String startingAnimationKey, MeshAbstractFactory meshFactory){

        mesh = meshFactory.createComplexMesh(modelPath);
        this.skeleton = meshFactory.createSkeleton(mesh.getData());

        for(Map.Entry<String, String> animationNamePathMapping : animationsKeysPathsMappings.entrySet()){

            String animationKey = animationNamePathMapping.getKey();
            String animationPath = animationNamePathMapping.getValue();

            AnimatedMeshable animation = meshFactory.createComplexAnimatedMesh(mesh, animationPath);
            animations.put(animationKey, animation);
        }

        actualAnimationName = startingAnimationKey;
        oldAnimationName = actualAnimationName;
        actualAnimation = animations.get(actualAnimationName);
        nextAnimation = actualAnimation;
        nextAnimationName = actualAnimationName;
    }

    @Override
    public void uploadToGpu() {

        mesh.uploadToGpu();

        for(AnimatedMeshable animatedMeshable : animations.values()){
            animatedMeshable.uploadToGpu();
        }
    }

    protected void setAnimation(String animationName){

        if(Objects.equals(oldAnimationName, actualAnimationName)){
           return;
        }

        oldAnimationName = animationName;
        actualAnimationName = animationName;

        actualAnimation.reset();
        actualAnimation = animations.get(animationName);
    }

    @Override
    public void setModel(Matrix4f model) {

        mesh.setModel(model);
    }

    @Override
    public void draw() {

        actualAnimation.draw();
    }

    @Override
    public void clear() {

        mesh.clear();
        actualAnimation.clear();
    }

    @Override
    public void update(double deltaTimeInSeconds) {

        setAnimation(actualAnimationName);
        actualAnimation.update(deltaTimeInSeconds);
        if(!Objects.equals(actualAnimationName, nextAnimationName)){
           nextAnimation.update(deltaTimeInSeconds);
        }
        this.deltaTimeInSeconds = deltaTimeInSeconds;
    }
}
