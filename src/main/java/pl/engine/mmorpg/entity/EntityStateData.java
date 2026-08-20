package pl.engine.mmorpg.entity;

public class EntityStateData {

    public boolean isWeaponHidden = true;
    public boolean isSprinting = true;
    public boolean isInAir = false;
    public boolean isGravityEnabled = false;
    public boolean canActionBeInterrupted = true;
    public double actionMinimumDuration = 0d;
    public EntityState entityState;
}
