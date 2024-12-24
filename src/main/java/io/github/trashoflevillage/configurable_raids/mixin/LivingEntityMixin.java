package io.github.trashoflevillage.configurable_raids.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import io.github.trashoflevillage.configurable_raids.access.RaidMixinAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyReturnValue(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("TAIL"))
    public boolean canTarget(boolean original, LivingEntity target) {
        if ((LivingEntity)(Object)this instanceof HostileEntity) {
            HostileEntity hostileEntity = (HostileEntity)(Object)this;
            if (((HostileEntityMixinAccess)hostileEntity).configurable_raids$hasActiveRaid()) {
                if (target instanceof HostileEntity) {
                    if (target instanceof RaiderEntity raider) {
                        return !raider.hasActiveRaid();
                    } else {
                        HostileEntityMixinAccess raider = (HostileEntityMixinAccess) target;
                        return target.getType() != EntityType.VEX && !raider.configurable_raids$hasActiveRaid();
                    }
                }
            }
            if (((HostileEntity) (Object) this instanceof RaiderEntity && ((RaiderEntity) (Object) this).hasActiveRaid())) {
                if (target instanceof HostileEntity) {
                    if (target instanceof RaiderEntity raider) {
                        return !raider.hasActiveRaid();
                    } else {
                        HostileEntityMixinAccess raider = (HostileEntityMixinAccess) target;
                        return !raider.configurable_raids$hasActiveRaid();
                    }
                }
            }
        }
        return original;
    }

    @Inject(method = "damage", at = @At("TAIL"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity)(Object)this instanceof HostileEntity && !((LivingEntity)(Object)this instanceof RaiderEntity)) {
            if (((HostileEntityMixinAccess)this).configurable_raids$hasActiveRaid()) {
                ((HostileEntityMixinAccess)this).configurable_raids$getRaid().updateBar();
            }
        }
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if ((LivingEntity)(Object)this instanceof HostileEntity) {
            HostileEntityMixinAccess entityAccess = (HostileEntityMixinAccess)(Object)this;
            if (!((HostileEntity) (Object) this instanceof RaiderEntity)) {
                if (((LivingEntity)(Object)this).getWorld() instanceof ServerWorld) {
                    Entity entity = damageSource.getAttacker();
                    Raid raid = entityAccess.configurable_raids$getRaid();
                    if (raid != null) {
                        if (entity != null && entity.getType() == EntityType.PLAYER) {
                            raid.addHero(entity);
                        }
                        ((RaidMixinAccess) raid).removeFromWave((HostileEntity) (Object) this, false);
                    }
                }
            }
        }
    }
}
