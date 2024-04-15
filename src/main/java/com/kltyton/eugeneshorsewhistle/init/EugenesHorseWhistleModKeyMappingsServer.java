package com.kltyton.eugeneshorsewhistle.init;

import com.kltyton.eugeneshorsewhistle.EugenesHorseWhistle;
import com.kltyton.eugeneshorsewhistle.network.WhistleMessage;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * 键位映射服务器类
 */
public class EugenesHorseWhistleModKeyMappingsServer {
	public static void serverLoad() {
		// 注册全局接收器
		ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(EugenesHorseWhistle.MODID, "whistle"), (server, entity, handler, buf, responseSender) -> WhistleMessage.apply(server, entity, buf));
	}
}