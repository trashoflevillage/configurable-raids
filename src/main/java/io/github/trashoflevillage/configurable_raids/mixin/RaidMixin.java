package io.github.trashoflevillage.configurable_raids.mixin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.trashoflevillage.configurable_raids.ConfigurableRaids;
import io.github.trashoflevillage.configurable_raids.RaiderData;
import io.github.trashoflevillage.configurable_raids.WaveData;
import io.github.trashoflevillage.configurable_raids.access.HostileEntityMixinAccess;
import io.github.trashoflevillage.configurable_raids.access.RaidMixinAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(Raid.class)
public abstract class RaidMixin implements RaidMixinAccess {
	@Shadow public abstract int getBadOmenLevel();
	@Shadow public abstract void updateBar();
	@Shadow public abstract void markDirty();
	@Shadow public abstract void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

	@Shadow private int wavesSpawned;
	@Shadow private float totalHealth;

	@Final @Shadow private ServerWorld world;
	@Shadow private Optional<BlockPos> preCalculatedRavagerSpawnLocation;
	@Final
	@Shadow private Random random;

	@Shadow public abstract void setWaveCaptain(int wave, RaiderEntity entity);

	@Unique
	private final Map<Integer, Set<HostileEntity>> waveToCustomRaiders = Maps.newHashMap();

	@Unique
	private boolean noLeader = false;

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

		boolean noLeader = true;

		// Spawn raiders
		for (RaiderData r : wave.raiders) {
			if (r.amount <= 0) {
				r.amount = 1;
			}

			for (int i = 0; i < r.amount; i++) {
				HostileEntity spawnedEntity = this.trySpawnRaider(currentWave, r, pos);
				if (spawnedEntity != null) {
					if (((HostileEntity)(Object)spawnedEntity) instanceof AbstractPiglinEntity) {
						AbstractPiglinEntity piglin = (AbstractPiglinEntity)(HostileEntity)(Object)spawnedEntity;
						piglin.setImmuneToZombification(true);
					}
				}
			}
		}

		this.preCalculatedRavagerSpawnLocation = Optional.empty();
		++this.wavesSpawned;
		this.updateBar();
		this.markDirty();
	}

	private HostileEntity trySpawnRaider(int wave, RaiderData raider, BlockPos pos) {
		if (raider.chance == 0.0F) {
			raider.chance = 1.0F;
		}

		if (raider.difficulty == null) {
			raider.difficulty = Difficulty.EASY;
		}

		if (this.world.getDifficulty().compareTo(raider.difficulty) >= 0 && this.random.nextFloat() < raider.chance) {
			HostileEntity entity = (HostileEntity) EntityType.get(raider.type).get().create(this.world);
			if (entity == null) return null;
			if (entity instanceof RaiderEntity) {
				RaiderEntity raiderEntity = (RaiderEntity) entity;

				if (noLeader && raiderEntity.canLead()) {
					noLeader = false;
					raiderEntity.setPatrolLeader(true);
					this.setWaveCaptain(wave, raiderEntity);
				}

				this.addRaider(wave, raiderEntity, pos, false);

				// Spawn passenger, if any exists.
				if (raider.rider != null) {
					HostileEntity newEntity = trySpawnRaider(wave, raider.rider, pos);
					if (newEntity != null) {
						newEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
						newEntity.startRiding(raiderEntity);
					}
				}
            } else {
				this.configurable_raids$addRaider(wave, entity, pos, false);

				// Spawn passenger, if any exists.
				if (raider.rider != null) {
					HostileEntity newEntity = trySpawnRaider(wave, raider.rider, pos);
					if (newEntity != null) {
						newEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
						newEntity.startRiding(entity);
					}
				}
            }
            return entity;
        }
		return null;
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


	@Unique
	public void configurable_raids$addRaider(int wave, HostileEntity raider, @Nullable BlockPos pos, boolean existing) {
		HostileEntityMixinAccess raiderAccess = (HostileEntityMixinAccess)raider;
		boolean bl = this.addToWave(wave, raider);
		if (bl) {
			raiderAccess.configurable_raids$setRaid((Raid)(Object)this);
			raiderAccess.configurable_raids$setWave(wave);
			raiderAccess.configurable_raids$setAbleToJoinRaid(true);
			raiderAccess.configurable_raids$setOutOfRaidCounter(0);
			if (!existing && pos != null) {
				raider.setPosition((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5);
				raider.initialize(this.world, this.world.getLocalDifficulty(pos), SpawnReason.EVENT, null);
				raider.setOnGround(true);
				this.world.spawnEntityAndPassengers(raider);
			}
		}
	}

	@Unique
	public boolean addToWave(int wave, HostileEntity raider) {
		return this.addToWave(wave, raider, true);
	}

	@Unique
	public boolean addToWave(int wave, HostileEntity entity, boolean countHealth) {
		this.waveToCustomRaiders.computeIfAbsent(wave, wavex -> Sets.newHashSet());
		Set<HostileEntity> set = this.waveToCustomRaiders.get(wave);
		HostileEntity raiderEntity = null;
		for (HostileEntity raiderEntity2 : set) {
			if (!raiderEntity2.getUuid().equals(entity.getUuid())) continue;
			raiderEntity = raiderEntity2;
			break;
		}
		if (raiderEntity != null) {
			set.remove(raiderEntity);
			set.add(entity);
		}
		set.add(entity);
		if (countHealth) {
			this.totalHealth += entity.getHealth();
		}
		this.updateBar();
		this.markDirty();
		return true;
	}

	@Unique
	public Set<HostileEntity> getAllCustomRaiders() {
		HashSet<HostileEntity> set = Sets.newHashSet();
		for (Set<HostileEntity> set2 : this.waveToCustomRaiders.values()) {
			set.addAll(set2);
		}
		return set;
	}

	@ModifyReturnValue(method = "getCurrentRaiderHealth", at = @At("TAIL"))
	public float getCurrentRaiderHealth(float original) {
		float f = 0.0f;
		for (Set<HostileEntity> set : this.waveToCustomRaiders.values()) {
			for (HostileEntity raiderEntity : set) {
				f += raiderEntity.getHealth();
			}
		}
		return f + original;
	}

	@ModifyReturnValue(method = "getRaiderCount", at = @At("TAIL"))
	public int getRaiderCount(int original) {
		return this.waveToCustomRaiders.values().stream().mapToInt(Set::size).sum() + original;
	}

	public void removeFromWave(HostileEntity entity, boolean countHealth) {
		boolean bl;
		Set<HostileEntity> set = this.waveToCustomRaiders.get(((HostileEntityMixinAccess)entity).configurable_raids$getWave());
		if (set != null && (bl = set.remove(entity))) {
			if (countHealth) {
				this.totalHealth -= entity.getHealth();
			}
			((HostileEntityMixinAccess)entity).configurable_raids$setRaid(null);
			this.updateBar();
			this.markDirty();
		}
	}
}