package io.github.trashoflevillage.configurable_raids.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import io.github.trashoflevillage.configurable_raids.access.RaidMixinAccess;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(HostileEntity.class)
public class HostileEntityMixin extends PathAwareEntity implements HostileEntityMixinAccess {
    @Unique
    @Nullable
    private BlockPos patrolTarget;
    @Unique
    private boolean patrolLeader;
    @Unique
    private boolean patrolling;
    @Unique
    @Nullable
    protected Raid raid;
    @Unique
    private boolean ableToJoinRaid;
    private int wave;
    private int outOfRaidCounter;

    protected HostileEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world, boolean patrolling) {
        super(entityType, world);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.patrolTarget != null) {
            nbt.put("PatrolTarget", NbtHelper.fromBlockPos(this.patrolTarget));
        }
        nbt.putBoolean("PatrolLeader", this.patrolLeader);
        nbt.putBoolean("Patrolling", this.patrolling);
        nbt.putInt("Wave", this.wave);
        nbt.putBoolean("CanJoinRaid", this.ableToJoinRaid);
        if (this.raid != null) {
            nbt.putInt("RaidId", this.raid.getRaidId());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("PatrolTarget")) {
            Optional<BlockPos> pos = NbtHelper.toBlockPos(nbt, "PatrolTarget");
            pos.ifPresent(blockPos -> this.patrolTarget = blockPos);
        }
        this.patrolLeader = nbt.getBoolean("PatrolLeader");
        this.patrolling = nbt.getBoolean("Patrolling");
        this.wave = nbt.getInt("Wave");
        this.ableToJoinRaid = nbt.getBoolean("CanJoinRaid");
        if (nbt.contains("RaidId", NbtElement.INT_TYPE)) {
            if (this.getWorld() instanceof ServerWorld) {
                this.raid = ((ServerWorld)this.getWorld()).getRaidManager().getRaid(nbt.getInt("RaidId"));
            }
            if (this.raid != null) {
                ((RaidMixinAccess)this.raid).addToWave(this.wave, (HostileEntity)(Object)this, false);
            }
        }
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        if (spawnReason != SpawnReason.PATROL && spawnReason != SpawnReason.EVENT && spawnReason != SpawnReason.STRUCTURE && world.getRandom().nextFloat() < 0.06f && this.configurable_raids$canLead()) {
            this.patrolLeader = true;
        }
        if (spawnReason == SpawnReason.PATROL) {
            this.patrolling = true;
        }
        this.configurable_raids$setAbleToJoinRaid(false);

        return super.initialize(world, difficulty, spawnReason, entityData);
    }
//
//    @Inject(method = "<init>", at = @At("TAIL"))
//    protected void init(EntityType entityType, World world, CallbackInfo ci) {
//        if (world != null && !world.isClient) {
//            this.initGoals();
//        }
//    }
//
//    protected void initGoals() {
//        HostileEntity entity = (HostileEntity)(Object)this;
//        if (!(entity instanceof RaiderEntity)) {
//            this.goalSelector.add(4, new GlobalPatrolGoal<>(entity, 0.7, 0.595));
//            this.goalSelector.add(3, new GlobalMoveToRaidCenterGoal<>(entity));
//            this.goalSelector.add(4, new GlobalAttackHomeGoal(entity, 1.05f, 1));
//        }
//    }

    @Override
    public boolean configurable_raids$isRaidCenterSet() {
        return this.patrolling;
    }

    @Override
    public void configurable_raids$setPatrolling(boolean patrolling) {
        this.patrolling = patrolling;
    }

    @Override
    public boolean configurable_raids$isPatrolLeader() {
        return patrolLeader;
    }

    @Override
    public boolean configurable_raids$hasPatrolTarget() {
        return patrolTarget != null;
    }

    @Override
    public BlockPos configurable_raids$getPatrolTarget() {
        return patrolTarget;
    }

    @Override
    public void configurable_raids$setPatrolTarget(BlockPos blockPos) {
        patrolTarget = blockPos;
    }

    @Override
    public void configurable_raids$setRandomPatrolTarget() {
        this.patrolTarget = this.getBlockPos().add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }

    @Override
    public boolean configurable_raids$hasNoRaid() {
        return true;
    }

    @Override
    public boolean configurable_raids$canLead() {
        return (HostileEntity)(Object)this instanceof PatrolEntity;
    }

    @Override
    public boolean configurable_raids$hasActiveRaid() {
        return this.configurable_raids$getRaid() != null && this.configurable_raids$getRaid().isActive();
    }

    @Override
    @Nullable
    public Raid configurable_raids$getRaid() {
        return this.raid;
    }

    @Override
    public void configurable_raids$setAbleToJoinRaid(boolean val) {
        ableToJoinRaid = val;
    }

    @Override
    public boolean configurable_raids$canJoinRaid() {
        return ableToJoinRaid;
    }

    @Override
    public void configurable_raids$setRaid(@Nullable Raid raid) {
        this.raid = raid;
    }

    @Override
    public void configurable_raids$setWave(int wave) {
        this.wave = wave;
    }

    @Override
    public void configurable_raids$setOutOfRaidCounter(int outOfRaidCounter) {
        this.outOfRaidCounter = outOfRaidCounter;
    }

    @Override
    public Integer configurable_raids$getWave() {
        return wave;
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld && this.isAlive()) {
            Raid raid = this.configurable_raids$getRaid();
            if (this.configurable_raids$canJoinRaid()) {
                if (raid == null) {
                    Raid raid2;
                    if (this.getWorld().getTime() % 20L == 0L && (raid2 = ((ServerWorld)this.getWorld()).getRaidAt(this.getBlockPos())) != null) {
                        ((RaidMixinAccess)raid2).configurable_raids$addRaider(raid2.getGroupsSpawned(), (HostileEntity)(Object)this, null, true);
                    }
                } else {
                    LivingEntity livingEntity = this.getTarget();
                    if (livingEntity != null && (livingEntity.getType() == EntityType.PLAYER || livingEntity.getType() == EntityType.IRON_GOLEM)) {
                        this.despawnCounter = 0;
                    }
                }
            }
        }
    }
}
