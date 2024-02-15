package com.kltyton.eugeneshorsewhistle.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "eugeneshorsewhistle")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public SearchRadius searchRadius = new SearchRadius();

    @ConfigEntry.Gui.CollapsibleObject
    public TeleportSettings teleportSettings = new TeleportSettings();

    public static class SearchRadius {
        public double entitySearchRadius = 128.0;
    }

    public static class TeleportSettings {
        public int maxTeleportOffset = 32;
        public double teleportDistanceThreshold = 64.0;
    }
}