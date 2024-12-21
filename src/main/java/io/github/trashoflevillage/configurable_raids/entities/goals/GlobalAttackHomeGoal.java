package io.github.trashoflevillage.configurable_raids.entities.goals;

import com.google.common.collect.Lists;
import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GlobalAttackHomeGoal extends Goal {
    private final HostileEntity raider;
    private final double speed;
    private BlockPos home;
    private final List<BlockPos> lastHomes = Lists.newArrayList();
    private final int distance;
    private boolean finished;

    public GlobalAttackHomeGoal(HostileEntity raider, double speed, int distance) {
        this.raider = raider;
        this.speed = speed;
        this.distance = distance;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.raider instanceof RaiderEntity) return false;
        this.purgeMemory();
        return this.isRaiding() && this.tryFindHome() && this.raider.getTarget() == null;
    }

    private boolean isRaiding() {
        return ((HostileEntityMixinAccess)(Object)this.raider).configurable_raids$hasActiveRaid() && !((HostileEntityMixinAccess)(Object)this.raider).configurable_raids$getRaid().isFinished();
    }

    private boolean tryFindHome() {
        ServerWorld serverWorld = (ServerWorld)this.raider.getWorld();
        BlockPos blockPos = this.raider.getBlockPos();
        Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.HOME), this::canLootHome, PointOfInterestStorage.OccupationStatus.ANY, blockPos, 48, this.raider.getRandom());
        if (optional.isEmpty()) {
            return false;
        }
        this.home = optional.get().toImmutable();
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (this.raider.getNavigation().isIdle()) {
            return false;
        }
        return this.raider.getTarget() == null && !this.home.isWithinDistance(this.raider.getPos(), (double)(this.raider.getWidth() + (float)this.distance)) && !this.finished;
    }

    @Override
    public void stop() {
        if (this.home.isWithinDistance(this.raider.getPos(), (double)this.distance)) {
            this.lastHomes.add(this.home);
        }
    }

    @Override
    public void start() {
        super.start();
        this.raider.setDespawnCounter(0);
        this.raider.getNavigation().startMovingTo(this.home.getX(), this.home.getY(), this.home.getZ(), this.speed);
        this.finished = false;
    }

    @Override
    public void tick() {
        if (this.raider.getNavigation().isIdle()) {
            Vec3d vec3d = Vec3d.ofBottomCenter(this.home);
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.raider, 16, 7, vec3d, 0.3141592741012573);
            if (vec3d2 == null) {
                vec3d2 = NoPenaltyTargeting.findTo(this.raider, 8, 7, vec3d, 1.5707963705062866);
            }
            if (vec3d2 == null) {
                this.finished = true;
                return;
            }
            this.raider.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
        }
    }

    private boolean canLootHome(BlockPos pos) {
        for (BlockPos blockPos : this.lastHomes) {
            if (!Objects.equals(pos, blockPos)) continue;
            return false;
        }
        return true;
    }

    private void purgeMemory() {
        if (this.lastHomes.size() > 2) {
            this.lastHomes.remove(0);
        }
    }
}
