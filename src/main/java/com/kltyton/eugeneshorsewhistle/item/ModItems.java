package com.kltyton.eugeneshorsewhistle.item;

import com.kltyton.eugeneshorsewhistle.EugenesHorseWhistle;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class ModItems {
    // 注册
    public static final Item ENDER_HORSE_ARMOR = registerItem(new EnderHorseArmorItem(15, "ender",
            new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));
    private static Item registerItem(Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(EugenesHorseWhistle.MODID, "ender_horse_armor"), item);
    }
    // 修改物品组
    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.addAfter(Items.DIAMOND_HORSE_ARMOR, ENDER_HORSE_ARMOR));
    }
    public static class EnderHorseArmorItem extends HorseArmorItem {
        private final ResourceLocation entityTexture;
        public EnderHorseArmorItem(int bonus, String name, Properties settings) {
            super(bonus, name, settings);
            // 设置马铠的材质路径
            this.entityTexture = new ResourceLocation(EugenesHorseWhistle.MODID,
                    "textures/entity/horse/armor/horse_armor_" + name + ".png");
        }
        // 获取马铠的材质路径
        @Override
        public ResourceLocation getTexture() {
            return this.entityTexture;
        }
    // 添加悬停提示
        @Override
        public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
            super.appendHoverText(itemstack, world, list, flag);
            list.add(Component.translatable("HoverText.ender_horse_armor"));
        }
    }
}