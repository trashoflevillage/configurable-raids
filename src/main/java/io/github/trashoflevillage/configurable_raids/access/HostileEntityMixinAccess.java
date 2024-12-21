package io.github.trashoflevillage.configurable_raids.access;

import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;

public interface HostileEntityMixinAccess {
    boolean configurable_raids$isRaidCenterSet();
    void configurable_raids$setPatrolling(boolean val);
    boolean configurable_raids$isPatrolLeader();
    boolean configurable_raids$hasPatrolTarget();
    BlockPos configurable_raids$getPatrolTarget();
    void configurable_raids$setPatrolTarget(BlockPos blockPos);
    void configurable_raids$setRandomPatrolTarget();
    boolean configurable_raids$hasNoRaid();
    boolean configurable_raids$canLead();
    boolean configurable_raids$hasActiveRaid();
    Raid configurable_raids$getRaid();
    void configurable_raids$setAbleToJoinRaid(boolean val);
    boolean configurable_raids$canJoinRaid();
    void configurable_raids$setRaid(Raid raid);
    void configurable_raids$setWave(int wave);
    void configurable_raids$setOutOfRaidCounter(int i);
}
