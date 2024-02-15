package com.kltyton.eugeneshorsewhistle.init;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

public class EugenesHorseWhistleModSounds {
	public static SoundEvent WHISTLE = SoundEvent.createVariableRangeEvent(new ResourceLocation("eugenes_horse_whistle", "whistle"));

	public static void load() {
		Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation("eugenes_horse_whistle", "whistle"), WHISTLE);
	}
}
