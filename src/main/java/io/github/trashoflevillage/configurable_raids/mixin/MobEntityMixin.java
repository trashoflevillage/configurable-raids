package io.github.trashoflevillage.configurable_raids.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import io.github.trashoflevillage.configurable_raids.entities.goals.GlobalAttackHomeGoal;
import io.github.trashoflevillage.configurable_raids.entities.goals.GlobalMoveToRaidCenterGoal;
import io.github.trashoflevillage.configurable_raids.entities.goals.GlobalPatrolGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Shadow GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityType entityType, World world, CallbackInfo ci) {
        if ((MobEntity)(Object)this instanceof HostileEntity && !((MobEntity)(Object)this instanceof RaiderEntity)) {
            HostileEntity entity = (HostileEntity)(Object) this;
            this.goalSelector.add(4, new GlobalPatrolGoal<>(entity, 0.7, 0.595));
            this.goalSelector.add(3, new GlobalMoveToRaidCenterGoal<>(entity));
            this.goalSelector.add(4, new GlobalAttackHomeGoal(entity, 1.05f, 1));
        }
    }

    @ModifyReturnValue(method = "cannotDespawn", at = @At("TAIL"))
    public boolean cannotDespawn(boolean original) {
        if ((MobEntity)(Object)this instanceof HostileEntity && !((MobEntity)(Object)this instanceof RaiderEntity)) {
            return original || ((HostileEntityMixinAccess)this).configurable_raids$hasActiveRaid();
        }
        return original;
    }
}
