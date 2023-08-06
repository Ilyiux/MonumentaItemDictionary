package dev.eliux.monumentaitemdictionary.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemFactory {
    private static final ItemStack ERROR_ITEM = fromEncoding("minecraft:red_concrete");

    public static ItemStack fromEncoding(String encoding) {
        try {
            Item item = Registries.ITEM.get(new Identifier(encoding));
            ItemStack stack = new ItemStack(item, 1);

            return stack;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ERROR_ITEM;
    }

    public static ItemStack fromEncodingWithNbt(String encoding, NbtCompound nbt) {
        try {
            Item item = Registries.ITEM.get(new Identifier(encoding));
            ItemStack stack = new ItemStack(item, 1);
            stack.getOrCreateNbt();
            stack.setNbt(nbt);

            return stack;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ERROR_ITEM;
    }
}
