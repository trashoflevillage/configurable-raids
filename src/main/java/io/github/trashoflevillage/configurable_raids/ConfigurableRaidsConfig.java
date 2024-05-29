package io.github.trashoflevillage.configurable_raids;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.Difficulty;

public class ConfigurableRaidsConfig {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String FILE_NAME = ConfigurableRaids.MOD_ID + ".json";
    public static final ConfigurableRaidsConfig INSTANCE =
            new ConfigurableRaidsConfig(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));

    protected final Path filePath;
    protected final File file;

    public ConfigurableRaidsConfig(Path filePath) {
        this.filePath = filePath;
        file = filePath.toFile();
    }

    public Map<Integer, ArrayList<WaveData>> fromJson() {
        Type type = new TypeToken<Map<Integer, ArrayList<WaveData>>>(){}.getType();
        Map<Integer, ArrayList<WaveData>> config;
        try {
            config = GSON.fromJson(Files.readString(filePath), type);
        } catch (IOException e) {
            config = new HashMap<>();
        }
        return config;
    }

    public static Map<Integer, ArrayList<WaveData>> getDefaultConfig() {
        Map<Integer, ArrayList<WaveData>> config = new HashMap<>();

        ArrayList<WaveData> waves = new ArrayList<>();
        ArrayList<WaveData> waves2 = new ArrayList<>();

        // BAD OMEN 0

        // WAVE 1
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
        );

        // WAVE 2
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 3)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
        );

        // WAVE 3
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 3)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:ravager"), 1)
                .addRaider(new RaiderData("minecraft:witch", 0.5f))
        );

        // WAVE 4
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"))
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch"), 3)
        );

        // WAVE 5
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 4)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 1)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:pillager"), 1)
        );

        // WAVE 6
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 1)
        );

        // WAVE 7
        waves.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 2)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 5)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch"), 1)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 2)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:vindicator"), 1)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:evoker"), 1)
        );


        // BAD OMEN 1+

        // WAVE 1
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
        );

        // WAVE 2
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 3)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
        );

        // WAVE 3
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 3)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:ravager"), 1)
                .addRaider(new RaiderData("minecraft:witch", 0.5f))
        );

        // WAVE 4
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"))
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch"), 3)
        );

        // WAVE 5
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 4)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 1)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:pillager"), 1)
        );

        // WAVE 6
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 4)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 2)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 1)
        );

        // WAVE 7
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 2)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 5)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch"), 1)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 2)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:vindicator"), 1)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:evoker"), 1)
        );

        // EXTRA WAVE / WAVE 8
        waves2.add(new WaveData()
                .addRaider(new RaiderData("minecraft:pillager"), 2)
                .addRaider(new RaiderData("minecraft:pillager", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:vindicator"), 5)
                .addRaider(new RaiderData("minecraft:vindicator", 0.5f), 2)
                .addRaider(new RaiderData("minecraft:witch"), 1)
                .addRaider(new RaiderData("minecraft:witch", 0.5f), 1)
                .addRaider(new RaiderData("minecraft:evoker"), 2)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:vindicator"), 1)
                .addRaider(new RaiderData("minecraft:ravager", 0.5f).setRider("minecraft:vindicator"), 1)
                .addRaider(new RaiderData("minecraft:ravager").setRider("minecraft:evoker"), 1)
        );

        config.put(0, waves);
        config.put(1, waves2);
        return config;
    }

    public void saveConfig(Map<Integer, ArrayList<WaveData>> config) {
        try {
            FileWriter writer = new FileWriter(file);
            Type typeObject = new TypeToken<HashMap>() {}.getType();
            String json = GSON.toJson(config, typeObject);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeConfig() {
        if (file.exists()) return;
        saveConfig(getDefaultConfig());
    }
}
