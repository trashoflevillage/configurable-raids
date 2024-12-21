package io.github.trashoflevillage.configurable_raids.access;

import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface RaidMixinAccess {
    void configurable_raids$addRaider(int wave, HostileEntity raider, @Nullable BlockPos pos, boolean existing);
}
