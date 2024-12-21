package io.github.trashoflevillage.configurable_raids.entities.goals;

import com.google.common.collect.Sets;
import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

public class GlobalMoveToRaidCenterGoal<T extends HostileEntity> extends Goal {
    private static final int FREE_RAIDER_CHECK_INTERVAL = 20;
    private static final float WALK_SPEED = 1.0f;
    private final T actor;
    private int nextFreeRaiderCheckAge;

    public GlobalMoveToRaidCenterGoal(T actor) {
        this.actor = actor;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return ((MobEntity)this.actor).getTarget() == null && !((Entity)this.actor).hasPassengers() && ((HostileEntityMixinAccess)this.actor).configurable_raids$hasActiveRaid() && !((HostileEntityMixinAccess)this.actor).configurable_raids$getRaid().isFinished() && !((ServerWorld)((Entity)this.actor).getWorld()).isNearOccupiedPointOfInterest(((Entity)this.actor).getBlockPos());
    }

    @Override
    public boolean shouldContinue() {
        return ((HostileEntityMixinAccess)this.actor).configurable_raids$hasActiveRaid() && !((HostileEntityMixinAccess)this.actor).configurable_raids$getRaid().isFinished() && ((Entity)this.actor).getWorld() instanceof ServerWorld && !((ServerWorld)((Entity)this.actor).getWorld()).isNearOccupiedPointOfInterest(((Entity)this.actor).getBlockPos());
    }

    @Override
    public void tick() {
        if (((HostileEntityMixinAccess)this.actor).configurable_raids$hasActiveRaid()) {
            Vec3d vec3d;
            Raid raid = ((HostileEntityMixinAccess)this.actor).configurable_raids$getRaid();
            if (((HostileEntity)this.actor).age > this.nextFreeRaiderCheckAge) {
                this.nextFreeRaiderCheckAge = ((HostileEntity)this.actor).age + 20;
                this.includeFreeRaiders(raid);
            }
            if (!((PathAwareEntity)this.actor).isNavigating() && (vec3d = NoPenaltyTargeting.findTo(this.actor, 15, 4, Vec3d.ofBottomCenter(raid.getCenter()), 1.5707963705062866)) != null) {
                ((MobEntity)this.actor).getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
        }
    }

    private void includeFreeRaiders(Raid raid) {
        if (raid.isActive()) {
            HashSet<RaiderEntity> set = Sets.newHashSet();
            List<RaiderEntity> list = ((Entity)this.actor).getWorld().getEntitiesByClass(RaiderEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0), raider -> !raider.hasActiveRaid() && RaidManager.isValidRaiderFor(raider, raid));
            set.addAll(list);
            for (RaiderEntity raiderEntity : set) {
                raid.addRaider(raid.getGroupsSpawned(), raiderEntity, null, true);
            }
        }
    }
}
