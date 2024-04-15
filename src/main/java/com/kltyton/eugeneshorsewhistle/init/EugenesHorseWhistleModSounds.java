package com.kltyton.eugeneshorsewhistle.init;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

// 声音加载类
public class EugenesHorseWhistleModSounds {
	// 声音
	public static SoundEvent WHISTLE = SoundEvent.createVariableRangeEvent(new ResourceLocation("eugenes_horse_whistle", "whistle"));
	// 方法
	public static void load() {
		// 注册
		Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation("eugenes_horse_whistle", "whistle"), WHISTLE);
	}
}
