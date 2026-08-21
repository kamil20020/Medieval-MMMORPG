package pl.engine.mmorpg.entity.combat;

import org.joml.Vector3f;
import pl.engine.mmorpg.animation.DynamicMesh;
import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityState;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.animation.AnimationComponent;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.mesh.MeshAbstractFactory;
import pl.engine.mmorpg.mesh.Meshable;

public class WeaponComponent implements Component {

    private final MeshAbstractFactory meshFactory;
    private final AnimationComponent animationComponent;
    private final EntityStateData entityStateData;
    private final InputData inputData;
    private DynamicMesh sword = null;
    private boolean isHiddingWeapon = false;

    public WeaponComponent(MeshAbstractFactory meshFactory, AnimationComponent animationComponent, EntityStateData entityStateData, InputData inputData){

        this.meshFactory = meshFactory;
        this.animationComponent = animationComponent;
        this.entityStateData = entityStateData;
        this.inputData = inputData;
    }

    @Override
    public void prepare() {

        Meshable swordMesh = meshFactory.createComplexMesh("models/weapons/sword.glb");
        sword = new DynamicMesh(swordMesh);
        animationComponent.addDynamicMesh(sword);
        hideWeapon();
    }

    private void hideWeapon(){

        sword.setBoneName("mixamorig:LeftUpLeg");
        sword.setRotation(new Vector3f((float) Math.toRadians(180), (float) Math.toRadians(90), 0));
        sword.setTranslation(new Vector3f(1.62f, 1f, 0.8f));
        sword.setScale(0.01f);
    }

    private void showWeapon(){

        sword.setBoneName("mixamorig:RightHand");
        sword.setRotation(new Vector3f((float) Math.toRadians(75), 0, 0));
        sword.setTranslation(new Vector3f(-0f, 0.1f, 0.4f));
        sword.setScale(0.01f);
    }

    @Override
    public void update(double deltaTime) {

        if(entityStateData.canActionBeInterrupted && isHiddingWeapon){

            isHiddingWeapon = false;
            hideWeapon();
        }

        if(!entityStateData.canActionBeInterrupted || !inputData.switchShowWeapon){
            return;
        }

        inputData.switchShowWeapon = false;
        entityStateData.canActionBeInterrupted = false;
        entityStateData.entityState = EntityState.EQUIP_WEAPON;
        entityStateData.isWeaponHidden = !entityStateData.isWeaponHidden;

        if(entityStateData.isWeaponHidden){

            isHiddingWeapon = true;
            entityStateData.actionMinimumDuration = 1.2d;
        }
        else{

            showWeapon();
            entityStateData.actionMinimumDuration = 0.7d;
        }
    }
}
