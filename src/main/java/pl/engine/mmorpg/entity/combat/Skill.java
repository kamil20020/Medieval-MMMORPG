package pl.engine.mmorpg.entity.combat;

public class Skill {

    private String animationName;
    private double animationDuration;

    public Skill(String animationName, double animationDuration){

        this.animationName = animationName;
        this.animationDuration = animationDuration;
    }

    public String getAnimationName() {

        return animationName;
    }

    public void setAnimationName(String animationName) {

        this.animationName = animationName;
    }

    public double getAnimationDuration() {

        return animationDuration;
    }

    public void setAnimationDuration(double animationDuration) {

        this.animationDuration = animationDuration;
    }
}
