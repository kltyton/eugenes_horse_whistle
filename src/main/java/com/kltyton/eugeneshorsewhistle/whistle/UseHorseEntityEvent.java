package com.kltyton.eugeneshorsewhistle.whistle;

import com.kltyton.eugeneshorsewhistle.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class UseHorseEntityEvent {
    public UseHorseEntityEvent() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // 检查玩家是否右键点击了一个马
            if (entity instanceof Horse tamedEntity) {
                // 检查玩家主手是否拿着末影马铠
                ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                boolean mainHandHasEnderHorseArmor = !mainHandItem.isEmpty() && mainHandItem.getItem() == ModItems.ENDER_HORSE_ARMOR;
                // 检查玩家副手是否拿着末影马铠
                ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
                boolean offHandHasEnderHorseArmor = !offHandItem.isEmpty() && offHandItem.getItem() == ModItems.ENDER_HORSE_ARMOR;
                // 如果玩家主手或者副手拿着末影马铠
                if (mainHandHasEnderHorseArmor || offHandHasEnderHorseArmor) {
                    if (tamedEntity.isTamed() && Objects.equals(tamedEntity.getOwnerUUID(), player.getUUID())) {
                        // 如果玩家主手拿着末影马铠
                        if (mainHandHasEnderHorseArmor) {
                            // 清除主手的末影马铠
                            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                        // 如果玩家副手拿着末影马铠
                        if (offHandHasEnderHorseArmor) {
                            // 清除副手的末影马铠
                            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                        }
                        // 给马装备末影马铠
                        Horse horse = (Horse) entity;
                        ItemStack oldArmor = horse.getItemBySlot(EquipmentSlot.CHEST);
                        // 如果马已经装备了其他马铠
                        if (!oldArmor.isEmpty()) {
                            // 把这个马铠添加到玩家的背包
                            if (!player.getInventory().add(oldArmor)) {
                                // 如果玩家的背包已满，就把马铠丢到地上
                                player.drop(oldArmor, false);
                            }
                        }
                        // 使用equipArmor方法给马装备末影马铠
                        horse.equipArmor(player, new ItemStack(ModItems.ENDER_HORSE_ARMOR));
                    }
                }
            }
            // 返回PASS，表示这个事件应该继续传递给其他的监听器
            return InteractionResult.PASS;
        });
    }
}
