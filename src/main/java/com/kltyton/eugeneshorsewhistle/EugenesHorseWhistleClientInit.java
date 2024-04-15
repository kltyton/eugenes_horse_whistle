package com.kltyton.eugeneshorsewhistle;

import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModKeyMappings;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ClientModInitializer;

@Environment(EnvType.CLIENT)
public class EugenesHorseWhistleClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EugenesHorseWhistleModKeyMappings.load();
	}
}
