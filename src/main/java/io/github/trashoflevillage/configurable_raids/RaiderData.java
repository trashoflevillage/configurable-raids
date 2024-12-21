package io.github.trashoflevillage.configurable_raids;

import net.minecraft.entity.EntityType;
import net.minecraft.world.Difficulty;

public class RaiderData {
    public String type;
    public float chance;
    public RaiderData rider;
    public int amount;
    public Difficulty difficulty;

    public RaiderData(String type, float chance) {
        this.type = type;
        this.chance = chance;
        this.amount = 1;
    }

    public RaiderData(String type, float chance, int amount) {
        this.type = type;
        this.chance = chance;
        this.amount = amount;
    }

    public RaiderData(String type) {
        this.type = type;
        this.chance = 1.0f;
    }

    public RaiderData setChance(float chance) {
        this.chance = chance;
        return this;
    }

    public RaiderData setRider(RaiderData data) {
        rider = data;
        return this;
    }

    public RaiderData setAmount(int amount) {
        amount = amount;
        return this;
    }

    public RaiderData setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }
}
