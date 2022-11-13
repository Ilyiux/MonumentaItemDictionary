package dev.eliux.monumentaitemdictionary.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.eliux.monumentaitemdictionary.util.ItemFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class SortButtonWidget extends ButtonWidget {
    public boolean enabled;
    private final Text message;
    private final ItemSortMenuGui.SortType sortType;
    private final String rawValue;

    private final ItemStack enabledIcon;
    private final ItemStack disabledIcon;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private final ItemSortMenuGui gui;

    public SortButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, boolean enabled, ItemSortMenuGui.SortType sortType, String rawValue, ItemSortMenuGui gui) {
        super(x, y, width, height, new LiteralText(""), onPress);

        this.rawValue = rawValue;
        this.sortType = sortType;
        this.gui = gui;
        this.message = message;
        this.enabled = enabled;

        enabledIcon = ItemFactory.fromEncoding("lime_terracotta");
        disabledIcon = ItemFactory.fromEncoding("red_terracotta");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean bl = this.clicked(mouseX, mouseY + gui.getSortScrollOffset(sortType));
                if (bl) {
                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);

        if (enabled) {
            switch (sortType) {
                case TYPE -> gui.controller.removeTypeFilter(rawValue);
                case REGION -> gui.controller.removeRegionFilter(rawValue);
                case TIER -> gui.controller.removeTierFilter(rawValue);
                case LOCATION -> gui.controller.removeLocationFilter(rawValue);
                case STAT -> gui.controller.removeStatFilter(rawValue);
            }
        } else {
            switch (sortType) {
                case TYPE -> gui.controller.addTypeFilter(rawValue);
                case REGION -> gui.controller.addRegionFilter(rawValue);
                case TIER -> gui.controller.addTierFilter(rawValue);
                case LOCATION -> gui.controller.addLocationFilter(rawValue);
                case STAT -> gui.controller.addStatFilter(rawValue);
            }
        }

        enabled = !enabled;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int verticalOffset = gui.getSortScrollOffset(sortType);
        boolean hovered = (mouseX >= x) && (mouseX <= x + 20) && (mouseY >= (y - verticalOffset)) && (mouseY <= (y - verticalOffset) + 20);

        if (y - verticalOffset < 0) return;
        if (y - verticalOffset > gui.height) return;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(hovered);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y - verticalOffset, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexture(matrices, this.x + this.width / 2, this.y - verticalOffset, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBackground(matrices, MinecraftClient.getInstance(), mouseX, mouseY);

        drawTextWithShadow(matrices, textRenderer, message, x + width + 5, y + 2 - verticalOffset, 0xFFFFFFFF);

        if (enabled) {
            MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(enabledIcon, x + 2, y + 2 - verticalOffset);
        } else {
            MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(disabledIcon, x + 2, y + 2 - verticalOffset);
        }
    }
}
