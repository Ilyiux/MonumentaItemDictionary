package dev.eliux.monumentaitemdictionary.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.eliux.monumentaitemdictionary.util.ItemColors;
import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.text.Text;

public class ItemButtonWidget extends ButtonWidget {
    private final int itemSize;
    private final DictionaryItem item;
    private final ItemStack builtItem;
    public final int index;

    private ItemDictionaryGui gui;

    public ItemButtonWidget(int x, int y, int itemSize, int index, Text message, PressAction onPress, DictionaryItem item, TooltipSupplier tooltipSupplier, ItemDictionaryGui gui) {
        super(x, y, itemSize, itemSize, message, onPress, tooltipSupplier);
        this.itemSize = itemSize;
        this.item = item;
        this.index = index;

        this.gui = gui;

        // dummy itemstack for rendering item icon
        builtItem = ItemFactory.fromEncoding(item.baseItem.split("/")[0].trim().toLowerCase().replace(" ", "_"));
        NbtCompound baseNbt = builtItem.getOrCreateNbt();
        NbtCompound plain = new NbtCompound();
        NbtCompound display = new NbtCompound();
        display.putString("Name", item.name);
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
        int yPixelOffset = -gui.getScrollPixels();

        // rendering breaks if I do not use this, what is this, why do I have to use this, I don't know
        RenderSystem.enableDepthTest();

        boolean hovered = (mouseX >= x) && (mouseX <= x + itemSize) && (mouseY >= (y + yPixelOffset)) && (mouseY <= (y + yPixelOffset) + itemSize);

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
