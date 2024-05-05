package com.kltyton.eugeneshorsewhistle.mixin;

import com.kltyton.eugeneshorsewhistle.event.PlayerStopRidingEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;stopRiding()V"))
    private void onPlayerStopRiding(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        Entity entity = player.getVehicle();
        PlayerStopRidingEvent.EVENT.invoker().onStopRiding(player, entity);
    }
}