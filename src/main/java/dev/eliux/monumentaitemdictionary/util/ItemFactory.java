package dev.eliux.monumentaitemdictionary.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
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

    public static ItemStack fromEncodingWithStringNbt(String encoding, String nbt) {
        NbtCompound compound;
        try {
            compound = StringNbtReader.parse(nbt);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();

            return ERROR_ITEM;
        }

        return fromEncodingWithNbt(encoding, compound);
    }

    public static void giveItemToClientPlayer(ItemStack item, int count) {
        if (MinecraftClient.getInstance().player != null) {
            ItemStack finalItem = item.copy();
            finalItem.setCount(count);
            MinecraftClient.getInstance().player.getInventory().insertStack(finalItem);
        }
    }
}
