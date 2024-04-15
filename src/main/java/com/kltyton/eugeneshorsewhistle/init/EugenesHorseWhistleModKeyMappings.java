package com.kltyton.eugeneshorsewhistle.init;

import com.kltyton.eugeneshorsewhistle.EugenesHorseWhistle;
import com.kltyton.eugeneshorsewhistle.network.WhistleMessage;
import org.lwjgl.glfw.GLFW;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.KeyMapping;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.mojang.blaze3d.platform.InputConstants;

//键位映射类
public class EugenesHorseWhistleModKeyMappings {
	public static class EugenesHorseWhistleModKeyMapping extends KeyMapping {
		private boolean isDownOld;

		public EugenesHorseWhistleModKeyMapping(String string, int i, String string2) {
			super(string, InputConstants.Type.KEYSYM, i, string2);
		}
		public int action() {
			if (isDownOld != isDown() && isDown()) {
				isDownOld = isDown();
				return 1;
			} else if (isDownOld != isDown() && !isDown()) {
				isDownOld = isDown();
				return 2;
			}
			isDownOld = isDown();
			return 0;
		}
	}
	public static EugenesHorseWhistleModKeyMapping WHISTLE = (EugenesHorseWhistleModKeyMapping) KeyBindingHelper
			.registerKeyBinding(new EugenesHorseWhistleModKeyMapping("key.eugenes_horse_whistle.whistle", GLFW.GLFW_KEY_H, "key.categories.gameplay"));
	public static void load() {
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			int WHISTLEaction = WHISTLE.action();
			if (WHISTLEaction == 1) {
				// 发送响笛消息，表示按下
				ClientPlayNetworking.send(new ResourceLocation(EugenesHorseWhistle.MODID, "whistle"), new WhistleMessage(true, false));
			} else if (WHISTLEaction == 2) {
				// 发送响笛消息，表示释放
				ClientPlayNetworking.send(new ResourceLocation(EugenesHorseWhistle.MODID, "whistle"), new WhistleMessage(false, true));
			}
		});
	}
}
