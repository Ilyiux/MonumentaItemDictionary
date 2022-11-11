package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemIconButtonWidget extends ButtonWidget {
    private ItemStack iconItem;

    public ItemIconButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier, String itemEncoding) {
        super(x, y, width, height, message, onPress, tooltipSupplier);

        iconItem = ItemFactory.fromEncoding(itemEncoding);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        MinecraftClient.getInstance().getItemRenderer().zOffset += 120;
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(iconItem, x + (width - 16) / 2, y + (width - 16) / 2);
        MinecraftClient.getInstance().getItemRenderer().zOffset -= 120;
    }
}
