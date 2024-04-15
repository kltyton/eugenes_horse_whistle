package com.kltyton.eugeneshorsewhistle.network;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.FriendlyByteBuf;

import com.kltyton.eugeneshorsewhistle.whistle.whistleandspur;

import io.netty.buffer.Unpooled;

public class WhistleMessage extends FriendlyByteBuf {
	public WhistleMessage(boolean pressed, boolean released) {
		super(Unpooled.buffer());
		writeBoolean(pressed);
		writeBoolean(released);
	}

	public static void apply(MinecraftServer server, ServerPlayer entity, FriendlyByteBuf buf) {
		boolean pressed = buf.readBoolean();
        server.execute(() -> {
			Level world = entity.level();
			if (pressed) {
				// 执行响笛和马刺
				whistleandspur.execute(world, entity);
			}
		});
	}
}