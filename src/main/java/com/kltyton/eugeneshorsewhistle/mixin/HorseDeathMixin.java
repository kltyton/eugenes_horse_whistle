package com.kltyton.eugeneshorsewhistle.mixin;

import com.kltyton.eugeneshorsewhistle.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.EquipmentSlot;

@Mixin(LivingEntity.class)
public class HorseDeathMixin {
    @Inject(method = "die", at = @At("HEAD"))
    public void onDeath(CallbackInfo info) {
        if ((Object) this instanceof Horse horse) {
            if (horse.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.ENDER_HORSE_ARMOR) {
                ((AbstractHorseAccessor)horse).getInventory().clearContent();
            }
        }
    }
}