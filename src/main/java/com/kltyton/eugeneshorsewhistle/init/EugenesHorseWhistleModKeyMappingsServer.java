package com.kltyton.eugeneshorsewhistle.init;

import com.kltyton.eugeneshorsewhistle.EugenesHorseWhistleMod;
import com.kltyton.eugeneshorsewhistle.network.WhistleMessage;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class EugenesHorseWhistleModKeyMappingsServer {
	public static void serverLoad() {
		ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(EugenesHorseWhistleMod.MODID, "whistle"), WhistleMessage::apply);
	}
}
