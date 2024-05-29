package io.github.trashoflevillage.configurable_raids;

import net.minecraft.world.Difficulty;

import java.util.ArrayList;

public class WaveData {
    public ArrayList<RaiderData> raiders;

    public WaveData() {
        raiders = new ArrayList<>();
    }

    public WaveData addRaider(RaiderData raider, int amount) {
        for (int j = 0; j < amount; j++) {
            raiders.add(raider);
        }
        return this;
    }

    public WaveData addRaider(RaiderData raider) {
        return addRaider(raider, 1);
    }
}
