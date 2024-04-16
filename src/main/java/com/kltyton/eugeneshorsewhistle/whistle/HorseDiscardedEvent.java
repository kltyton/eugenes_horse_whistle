package com.kltyton.eugeneshorsewhistle.whistle;

import com.kltyton.eugeneshorsewhistle.event.PlayerStopRidingEvent;
import com.kltyton.eugeneshorsewhistle.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import java.util.Objects;

public class HorseDiscardedEvent {
    // 使用全局的horsenbtdata对象
    private static final HorseNbtDataSaveEvent HORSE_NBT_DATA = new HorseNbtDataSaveEvent();

    public HorseDiscardedEvent() {
        PlayerStopRidingEvent.EVENT.register((player, entity) -> {
            if (entity instanceof AbstractHorse tamedEntity) {
                if (tamedEntity.isTamed() && Objects.equals(tamedEntity.getOwnerUUID(), player.getUUID())) {
                    boolean hasEnderHorseArmor = false;
                    ItemStack armor = tamedEntity.getItemBySlot(EquipmentSlot.CHEST);
                    if (!armor.isEmpty() && armor.getItem() == ModItems.ENDER_HORSE_ARMOR) {
                        hasEnderHorseArmor = true;
                    }
                    if (hasEnderHorseArmor) {
                        // 保存马实体的NBT数据
                        HORSE_NBT_DATA.saveNbtData(tamedEntity);
                        // 移除马实体
                        tamedEntity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
            }
        });
    }
    // 获取全局的horsenbtdata对象
    public static HorseNbtDataSaveEvent getHorseNbtData() {
        return HORSE_NBT_DATA;
    }
}
