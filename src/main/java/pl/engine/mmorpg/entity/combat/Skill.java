package pl.engine.mmorpg.entity.combat;

public abstract class Skill {

    private SkillType skillType;
    private double animationDuration;

    public Skill(SkillType skillType, double animationDuration){

        this.skillType = skillType;
        this.animationDuration = animationDuration;
    }

    public SkillType getSkillType() {

        return skillType;
    }

    public void setSkillType(SkillType skillType) {

        this.skillType = skillType;
    }

    public double getAnimationDuration() {

        return animationDuration;
    }

    public void setAnimationDuration(double animationDuration) {

        this.animationDuration = animationDuration;
    }

    public abstract void update(double deltaTime);

    public boolean hasOwnAnimation(){

        return true;
    }
}
