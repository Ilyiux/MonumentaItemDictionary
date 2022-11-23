package dev.eliux.monumentaitemdictionary.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.eliux.monumentaitemdictionary.util.ItemColors;
import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import dev.eliux.monumentaitemdictionary.util.ItemStat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.text.Text;

import java.util.ArrayList;

public class ItemButtonWidget extends ButtonWidget {
    private final int itemSize;
    private final DictionaryItem item;
    private final ItemStack builtItem;
    public final int index;

    public int shownMasterworkTier;

    private ItemDictionaryGui gui;

    public ItemButtonWidget(int x, int y, int itemSize, int index, Text message, PressAction onPress, DictionaryItem item, TooltipSupplier tooltipSupplier, ItemDictionaryGui gui) {
        super(x, y, itemSize, itemSize, message, onPress, tooltipSupplier);
        this.itemSize = itemSize;
        this.item = item;
        this.index = index;
        shownMasterworkTier = item.getMinMasterwork();

        this.gui = gui;

        // dummy itemstack for rendering item icon
        builtItem = ItemFactory.fromEncoding(item.baseItem.split("/")[0].trim().toLowerCase().replace(" ", "_"));
        NbtCompound baseNbt = builtItem.getOrCreateNbt();
        NbtCompound plain = new NbtCompound();
        NbtCompound display = new NbtCompound();
        display.putString("Name", item.name.split("\\(")[0].trim());
        plain.put("display", display);
        baseNbt.put("plain", plain);
        builtItem.setNbt(baseNbt);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);


    }

    public boolean isItem(DictionaryItem item) {
        return this.item == item;
    }

    public void setMinimumMasterwork() {
        shownMasterworkTier = item.getMinMasterwork();
    }

    public void setMaximumMasterwork() {
        // shownMasterworkTier = item.getMaxMasterwork();
        int max = 0;
        for (ArrayList<ItemStat> stats : item.stats) {
            if (stats != null) max = item.stats.indexOf(stats);
        }
        shownMasterworkTier = max;
    }

    public void scrolled(double mouseX, double mouseY, double amount) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y - gui.getScrollPixels() && mouseY <= y + height - gui.getScrollPixels() && item.hasMasterwork) {
            shownMasterworkTier += amount;
            if (shownMasterworkTier < 0) shownMasterworkTier = 0;
            if (shownMasterworkTier > item.getMaxMasterwork() - 1) shownMasterworkTier = item.getMaxMasterwork() - 1;
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int yPixelOffset = -gui.getScrollPixels();

        // rendering breaks if I do not use this, what is this, why do I have to use this, I don't know
        RenderSystem.enableDepthTest();

        boolean hovered = (mouseX >= x) && (mouseX <= x + itemSize) && (mouseY >= (y + yPixelOffset)) && (mouseY <= (y + yPixelOffset) + itemSize) && (mouseY > gui.labelMenuHeight);

        int outlineColor = hovered ? 0xFFC6C6C6 : 0xFFFFFFFF;
        int fillOpacity = hovered ? 0x6B000000 : 0x88000000;

        fill(matrices, x, (y + yPixelOffset), x + width, (y + yPixelOffset) + width, fillOpacity + ItemColors.getColorForTier(item.tier));
        drawHorizontalLine(matrices, x, x + width, (y + yPixelOffset), outlineColor);
        drawHorizontalLine(matrices, x, x + width, (y + yPixelOffset) + height, outlineColor);
        drawVerticalLine(matrices, x, (y + yPixelOffset), (y + yPixelOffset) + height, outlineColor);
        drawVerticalLine(matrices, x + width, (y + yPixelOffset), (y + yPixelOffset) + height, outlineColor);

        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(builtItem, x + (itemSize / 2) - 7, (y + yPixelOffset) + (itemSize / 2) - 7);

        if (hovered)
            renderTooltip(matrices, mouseX, mouseY);
    }
}
