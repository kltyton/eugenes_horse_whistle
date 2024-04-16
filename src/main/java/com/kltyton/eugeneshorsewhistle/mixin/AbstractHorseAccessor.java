package com.kltyton.eugeneshorsewhistle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.SimpleContainer;

@Mixin(AbstractHorse.class)
public interface AbstractHorseAccessor {
    @Accessor("inventory")
    SimpleContainer getInventory();
}