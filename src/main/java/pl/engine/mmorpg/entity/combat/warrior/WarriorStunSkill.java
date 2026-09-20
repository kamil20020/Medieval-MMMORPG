package pl.engine.mmorpg.entity.combat.warrior;

import pl.engine.mmorpg.entity.combat.Skill;
import pl.engine.mmorpg.entity.combat.SkillType;

public class WarriorStunSkill extends Skill {

    public WarriorStunSkill() {

        super(SkillType.WARRIOR_STUN, 1);
    }

    @Override
    public void update(double deltaTime) {

    }
}
