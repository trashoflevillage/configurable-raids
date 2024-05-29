package io.github.trashoflevillage.configurable_raids.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.trashoflevillage.configurable_raids.ConfigurableRaids;
import io.github.trashoflevillage.configurable_raids.RaiderData;
import io.github.trashoflevillage.configurable_raids.WaveData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Optional;

@Mixin(Raid.class)
public abstract class RaidMixin {
	@Shadow public abstract int getBadOmenLevel();
	@Shadow public abstract void updateBar();
	@Shadow public abstract void markDirty();
	@Shadow public abstract void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

	@Shadow private int wavesSpawned;
	@Shadow private float totalHealth;

	@Final @Shadow private ServerWorld world;
	@Shadow private Optional<BlockPos> preCalculatedRavagerSpawnLocation;
	@Shadow private Random random;

	@Shadow public abstract void setWaveCaptain(int wave, RaiderEntity entity);

	/**
	 * @author trashoflevillage
	 * @reason Overriding the raid enemies that spawn.
	 */
	@Overwrite
	private void spawnNextWave(BlockPos pos) {
		boolean bl = false;
		this.totalHealth = 0.0F;
		LocalDifficulty localDifficulty = this.world.getLocalDifficulty(pos);

		int omen = getBadOmenLevel() - 1;
		while (!ConfigurableRaids.WAVES.containsKey(omen)) {
			if (omen <= 0) {
				omen = 0;
				break;
			}
			omen--;
		}

		int currentWave = this.wavesSpawned;
		WaveData wave = ConfigurableRaids.WAVES.get(omen).get(currentWave);

		ArrayList<RaiderEntity> entities = new ArrayList<>();
		boolean noLeader = true;

		// Spawn raiders
		for (RaiderData r : wave.raiders) {
			// Check if the mob should actually spawn.
			if (this.random.nextFloat() < r.chance) {
				RaiderEntity raider = (RaiderEntity)EntityType.get(r.type).get().create(this.world);

				// For some reason, vanilla raid code checks this.
				// I don't know why this needs a null check, and frankly, I'm not risking removing it.
				if (raider == null) break;

				if (noLeader && raider.canLead()) {
					noLeader = false;
					raider.setPatrolLeader(true);
					this.setWaveCaptain(currentWave, raider);
				}

				this.addRaider(currentWave, raider, pos, false);

				// Spawn passenger, if any exists.
				if (r.rider != null) {
					RaiderEntity raider2 = (RaiderEntity)EntityType.get(r.rider).get().create(this.world);
					if (raider2 == null) break;

					if (noLeader && raider2.canLead()) {
						noLeader = false;
						raider2.setPatrolLeader(true);
						this.setWaveCaptain(currentWave, raider2);
					}

					this.addRaider(currentWave, raider2, pos, false);
					raider2.refreshPositionAndAngles(pos, 0.0F, 0.0F);
					raider2.startRiding(raider);
				}
			}
		}

		this.preCalculatedRavagerSpawnLocation = Optional.empty();
		++this.wavesSpawned;
		this.updateBar();
		this.markDirty();
	}

	@ModifyReturnValue(method = "getMaxWaves", at = @At("RETURN"))
	public int getMaxWaves(int original) {
		int omen = getBadOmenLevel();
		while (!ConfigurableRaids.WAVES.containsKey(omen)) {
			if (omen <= 0) {
				omen = 0;
				break;
			}
			omen--;
		}

		return ConfigurableRaids.WAVES.get(omen).size();
	}

	@ModifyReturnValue(method = "hasExtraWave", at = @At("RETURN"))
	private boolean hasExtraWave(boolean original) {
		return false;
	}
}