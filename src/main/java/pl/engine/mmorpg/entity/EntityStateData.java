package pl.engine.mmorpg.entity;

public class EntityStateData {

    public boolean isCrouching = false;
    public boolean isWeaponHidden = false;
    public boolean isSprinting = true;
    public boolean isInAir = false;
    public boolean isGravityEnabled = false;
    public boolean canActionBeInterrupted = true;
    public double actionMinimumDuration = 0d;
    public EntityState entityState;
}
