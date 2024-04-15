package com.kltyton.eugeneshorsewhistle.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "eugenes_horse_whistle")
public class ModConfig implements ConfigData {
    public double entitySearchRadius = 128.0;
    public int maxTeleportOffset = 32;
    public double teleportDistanceThreshold = 64.0;
    public boolean playWhistleSound = true;
    public boolean spurs = true;

    public double getEntitySearchRadius() {
        return entitySearchRadius;
    }

    public int getMaxTeleportOffset() {
        return maxTeleportOffset;
    }

    public double getTeleportDistanceThreshold() {
        return teleportDistanceThreshold;
    }

    public boolean shouldPlayWhistleSound() {
        return playWhistleSound;
    }

    public boolean isSpurs() {
        return spurs;
    }

}