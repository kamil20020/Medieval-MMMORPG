package pl.engine.mmorpg.entity.combat;

import pl.engine.mmorpg.entity.Component;
import pl.engine.mmorpg.entity.EntityStateData;
import pl.engine.mmorpg.entity.TransformComponent;
import pl.engine.mmorpg.entity.animation.AnimationComponent;
import pl.engine.mmorpg.entity.input.InputData;
import pl.engine.mmorpg.entity.move.MovementComponent;

import java.util.ArrayList;
import java.util.List;

public class SkillComponent implements Component {

    private final EntityStateData entityStateData;
    private final InputData inputData;
    private final AnimationComponent animationComponent;
    private final MovementComponent movementComponent;
    private final TransformComponent transformComponent;

    private Skill usedSkill = null;
    private final List<Skill> skills;

    public SkillComponent(EntityStateData entityStateData, InputData inputData, AnimationComponent animationComponent, MovementComponent movementComponent, TransformComponent transformComponent){

        this.entityStateData = entityStateData;
        this.inputData = inputData;
        this.animationComponent = animationComponent;
        this.movementComponent = movementComponent;
        this.transformComponent = transformComponent;
        this.skills = getSkills();
    }

    private List<Skill> getSkills(){

        List<Skill> results = new ArrayList<>();

        Skill slash = new WarriorSlashSkill(movementComponent, transformComponent);
        results.add(slash);

        Skill spin = new WarriorSpinSkill(movementComponent, transformComponent);
        results.add(spin);

        Skill stun = new WarriorStunSkill();
        results.add(stun);

        Skill dash = new WarriorDashSkill(movementComponent, transformComponent);
        results.add(dash);

        Skill dodge = new WarriorDodgeSkill(inputData, entityStateData,  movementComponent, transformComponent);
        results.add(dodge);

        return results;
    }

    @Override
    public void update(double deltaTime) {

        if(entityStateData.isWeaponHidden){
            return;
        }

        if(entityStateData.canActionBeInterrupted && usedSkill != null){

            usedSkill = null;
            return;
        }

        if(usedSkill != null){

            usedSkill.update(deltaTime);
            return;
        }

        if(!entityStateData.canActionBeInterrupted || inputData.skillIndex == null){
            return;
        }

        Skill skill = skills.get(inputData.skillIndex);
        SkillType skillType = skill.getSkillType();

        if(skill.hasOwnAnimation()){

            String skillAnimationName = AnimationComponent.getKey(skillType);
            animationComponent.setBlockingAnimation(skillAnimationName);
            entityStateData.actionMinimumDuration = skill.getAnimationDuration();
            entityStateData.canActionBeInterrupted = false;
        }

        usedSkill = skill;
        inputData.skillIndex = null;

        usedSkill.update(deltaTime);
    }
}
