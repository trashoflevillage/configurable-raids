package io.github.trashoflevillage.configurable_raids.entities.goals;


import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

import java.util.EnumSet;
import java.util.List;

public class GlobalPatrolGoal<T extends HostileEntity>
        extends Goal {
    private static final int field_30474 = 200;
    private final T entity;
    private final double leaderSpeed;
    private final double followSpeed;
    private long nextPatrolSearchTime;

    public GlobalPatrolGoal(T entity, double leaderSpeed, double followSpeed) {
        this.entity = entity;
        this.leaderSpeed = leaderSpeed;
        this.followSpeed = followSpeed;
        this.nextPatrolSearchTime = -1L;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (entity instanceof PatrolEntity) return false;
        boolean bl = ((Entity)this.entity).getWorld().getTime() < this.nextPatrolSearchTime;
        return ((HostileEntityMixinAccess)this.entity).configurable_raids$isRaidCenterSet() && ((MobEntity)this.entity).getTarget() == null && !((Entity)this.entity).hasPassengers() && ((HostileEntityMixinAccess)this.entity).configurable_raids$hasPatrolTarget() && !bl;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        boolean bl = ((HostileEntityMixinAccess)this.entity).configurable_raids$isPatrolLeader();
        EntityNavigation entityNavigation = ((MobEntity)this.entity).getNavigation();
        if (entityNavigation.isIdle()) {
            List<HostileEntity> list = this.findPatrolTargets();
            if (((HostileEntityMixinAccess)this.entity).configurable_raids$isRaidCenterSet() && list.isEmpty()) {
                ((HostileEntityMixinAccess)this.entity).configurable_raids$setPatrolling(false);
            } else if (!bl || !((HostileEntityMixinAccess)this.entity).configurable_raids$getPatrolTarget().isWithinDistance(((Entity)this.entity).getPos(), 10.0)) {
                Vec3d vec3d = Vec3d.ofBottomCenter(((HostileEntityMixinAccess)this.entity).configurable_raids$getPatrolTarget());
                Vec3d vec3d2 = ((Entity)this.entity).getPos();
                Vec3d vec3d3 = vec3d2.subtract(vec3d);
                vec3d = vec3d3.rotateY(90.0f).multiply(0.4).add(vec3d);
                Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
                BlockPos blockPos = BlockPos.ofFloored(vec3d4);
                blockPos = ((Entity)this.entity).getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
                if (!entityNavigation.startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), bl ? this.followSpeed : this.leaderSpeed)) {
                    this.wander();
                    this.nextPatrolSearchTime = ((Entity)this.entity).getWorld().getTime() + 200L;
                } else if (bl) {
                    for (HostileEntity entity : list) {
                        ((HostileEntityMixinAccess)entity).configurable_raids$setPatrolTarget(blockPos);
                    }
                }
            } else {
                ((HostileEntityMixinAccess)this.entity).configurable_raids$setRandomPatrolTarget();
            }
        }
    }

    private List<HostileEntity> findPatrolTargets() {
        return ((Entity)this.entity).getWorld().getEntitiesByClass(HostileEntity.class, ((Entity)this.entity).getBoundingBox().expand(16.0), entity -> ((HostileEntityMixinAccess)entity).configurable_raids$hasNoRaid() && !entity.isPartOf((Entity)this.entity));
    }

    private boolean wander() {
        Random random = ((LivingEntity)this.entity).getRandom();
        BlockPos blockPos = ((Entity)this.entity).getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ((Entity)this.entity).getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        return ((MobEntity)this.entity).getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.leaderSpeed);
    }
}
