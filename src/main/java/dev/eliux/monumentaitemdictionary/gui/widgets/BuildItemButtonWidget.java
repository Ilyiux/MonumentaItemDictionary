package dev.eliux.monumentaitemdictionary.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.eliux.monumentaitemdictionary.gui.builder.BuilderGui;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.util.ItemColours;
import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Math.ceil;

public class BuildItemButtonWidget extends ButtonWidget {
    private final Supplier<List<Text>> lore;
    public final DictionaryItem item;
    private final BuilderGui gui;
    private final ItemStack builtItem;
    private final float scale;

    public BuildItemButtonWidget(int x, int y, int itemSize, Text message, PressAction onPress, DictionaryItem item, Supplier<List<Text>> lore, BuilderGui gui) {
        super(x, y, itemSize, itemSize, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.lore = lore;
        this.item = item;
        this.gui = gui;
        this.scale = (float) width/18;


        builtItem = ItemFactory.fromEncoding(item != null ? (item.baseItem.split("/")[0].trim().toLowerCase().replace(" ", "_")) : "barrier");
        NbtCompound baseNbt = builtItem.getOrCreateNbt();
        NbtCompound plain = new NbtCompound();
        NbtCompound display = new NbtCompound();
        display.putString("Name", item != null ? (item.name.split("\\(")[0].trim()) : "No Item");
        plain.put("display", display);
        baseNbt.put("plain", plain);
        builtItem.setNbt(baseNbt);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.enableDepthTest();

        int minX = getX();
        int minY = getY();
        int maxX = minX + width;
        int maxY = minY + height;
        int itemSize = (int) (16*scale);

        boolean hovered = (mouseX >= minX) && (mouseX <= maxX) && (mouseY >= minY) && (mouseY <= maxY) && (mouseY > gui.labelMenuHeight);

        int outlineColor = hovered ?  0xFFC6C6C6 : 0xFFFFFFFF;
        int fillOpacity = hovered ? 0x6B000000 : 0x88000000;

        fill(matrices, minX, minY, maxX, maxY, fillOpacity | (gui.itemOnBuildButton == item && item != null ? 0x000FF000 : 0x00000000) | (item != null ? (ItemColours.getColorForTier(item.hasMasterwork ? item.getTierFromMasterwork(item.getMinMasterwork()) : item.getTierNoMasterwork())) : 0x00000000));
        drawHorizontalLine(matrices, minX, maxX, minY, outlineColor);
        drawHorizontalLine(matrices, minX, maxX, maxY, outlineColor);
        drawVerticalLine(matrices, minX, minY, maxY, outlineColor);
        drawVerticalLine(matrices, maxX, minY, maxY, outlineColor);

        matrices.push();
        matrices.scale(scale, scale, scale);
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(matrices, builtItem, (int) ceil((minX + (double) width/2 - ceil(
                (double) itemSize/2))/scale), (int) ceil((minY + (double) height/2 - ceil((double) itemSize/2))/scale));
        matrices.pop();

        if (hovered) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.literal("Click to add an item."));
            gui.renderTooltip(matrices, (item != null ? lore.get() : lines), mouseX, mouseY);
        }
    }
}
