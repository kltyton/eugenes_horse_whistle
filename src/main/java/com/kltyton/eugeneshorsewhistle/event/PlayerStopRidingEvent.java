package com.kltyton.eugeneshorsewhistle.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface PlayerStopRidingEvent {
    Event<PlayerStopRidingEvent> EVENT = EventFactory.createArrayBacked(PlayerStopRidingEvent.class,
            (listeners) -> (player, entity) -> {
                for (PlayerStopRidingEvent event : listeners) {
                    event.onStopRiding(player, entity);
                }
            }
    );
    void onStopRiding(Player player, Entity entity);
}

