package dev.eliux.monumentaitemdictionary.gui.widgets;

import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.List;

public class ItemIconButtonWidget extends ButtonWidget {
    private final ItemStack iconItem;
    private final List<Text> tooltipText;

    public ItemIconButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, Text tooltipText, String itemEncoding, String displayInfo) {
        this(x, y, width, height, message, onPress, List.of(tooltipText), itemEncoding, displayInfo);
    }

    public ItemIconButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, List<Text> tooltipText, String itemEncoding, String displayInfo) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.tooltipText = tooltipText;

        iconItem = ItemFactory.fromEncoding(itemEncoding);
        if (!displayInfo.equals("")) {
            NbtCompound baseNbt = iconItem.getOrCreateNbt();
            NbtCompound plain = new NbtCompound();
            NbtCompound display = new NbtCompound();
            display.putString("Name", displayInfo);
            plain.put("display", display);
            baseNbt.put("plain", plain);
            iconItem.setNbt(baseNbt);
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.translate(0, 0, 120);
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(matrices, iconItem, getX() + (width - 16) / 2, getY() + (width - 16) / 2);
        matrices.pop();

        if (isHovered() && MinecraftClient.getInstance().currentScreen != null) {
            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, tooltipText, mouseX, mouseY);
        }
    }
}
