package io.github.trashoflevillage.configurable_raids;

import net.minecraft.entity.EntityType;
import net.minecraft.world.Difficulty;

public class RaiderData {
    public String type;
    public float chance;
    public String rider;

    public RaiderData(String type, float chance) {
        this.type = type;
        this.chance = chance;
    }

    public RaiderData(String type) {
        this.type = type;
        this.chance = 1.0f;
    }

    public RaiderData setChance(float chance) {
        this.chance = chance;
        return this;
    }

    public RaiderData setRider(String type) {
        rider = type;
        return this;
    }
}
