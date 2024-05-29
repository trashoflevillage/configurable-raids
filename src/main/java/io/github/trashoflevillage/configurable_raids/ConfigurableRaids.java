package io.github.trashoflevillage.configurable_raids;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.minecraft.world.Difficulty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigurableRaids implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "configurable_raids";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Map<Integer, ArrayList<WaveData>> WAVES;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Configurable Raids");
		ConfigurableRaidsConfig.INSTANCE.initializeConfig();
		WAVES = ConfigurableRaidsConfig.INSTANCE.fromJson();
		LOGGER.info("Configurable Raids initialized!");
	}
}