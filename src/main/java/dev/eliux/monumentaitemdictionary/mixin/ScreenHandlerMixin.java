package dev.eliux.monumentaitemdictionary.mixin;

import dev.eliux.monumentaitemdictionary.Mid;
import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.builder.DictionaryBuild;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(Screen.class)
public abstract class ScreenHandlerMixin  extends AbstractParentElement {
    @Shadow @Final private List<Selectable> selectables;
    @Shadow @Final private List<Element> children;

    @Shadow protected abstract <T extends Element & Selectable> T addSelectableChild(T child);

    @Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Shadow public abstract void close();

    @Unique private ButtonWidget buildFromInventoryButton = null;
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (buildFromInventoryButton != null) {
            buildFromInventoryButton.renderButton(matrices, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    private void init(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (client.currentScreen instanceof GenericContainerScreen && Objects.requireNonNull(client.currentScreen).getTitle().getString().equals("Player Stats Calculator")) {
            int x = ((HandledScreenAccessor) client.currentScreen).getX() + ((HandledScreenAccessor) client.currentScreen).getBackGroundWidth() - 20;
            int y = ((HandledScreenAccessor) client.currentScreen).getY() - 20;
            buildFromInventoryButton =
                    new ItemIconButtonWidget(x, y, 20, 20, Text.literal(""), button -> {
                        DictionaryController controller = new DictionaryController();
                        DefaultedList<Slot> slots = ((HandledScreenAccessor) client.currentScreen).getHandler().slots;
                        List<Integer> slotsInOrderOfBuild = Arrays.asList(52, 25, 26, 35, 44, 53);
                        List<DictionaryItem> itemsFromBuild = new ArrayList<>();
                        String region = slots.get(4).getStack().getName().getString();
                        if (region.contains("Ring")) region = "Ring";
                        else if (region.contains("Isles")) region = "Isles";
                        else if (region.contains("Valley")) region = "Valley";

                        for (int slot : slotsInOrderOfBuild) {
                            String itemName = slots.get(slot).getStack().getName().getString();
                            boolean isExalted =
                                    slots.get(slot).getStack().getTooltip(client.player, TooltipContext.BASIC).stream()
                                            .anyMatch(text -> text.getString().contains("Ring"));
                            itemsFromBuild.add(controller.getItemByName(itemName, isExalted));
                        }

                        this.close();
                        int id = controller.generateNewId();
                        controller.setBuilderScreen();
                        controller.builderGui.loadItems(new DictionaryBuild("", itemsFromBuild, List.of(), null, region, "", "", false, id));
                    }, Text.literal("Add build from current inventory"), "name_tag", "");
            addSelectableChild(buildFromInventoryButton);
            addDrawableChild(buildFromInventoryButton);
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        buildFromInventoryButton = null;
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void onResize(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (buildFromInventoryButton != null && client.currentScreen != null) {
            int x = ((HandledScreenAccessor) client.currentScreen).getX() + ((HandledScreenAccessor) client.currentScreen).getBackGroundWidth() - 20;
            int y = ((HandledScreenAccessor) client.currentScreen).getY() - 20;
            buildFromInventoryButton.setX(x);
            buildFromInventoryButton.setY(y);
            addSelectableChild(buildFromInventoryButton);
            addDrawableChild(buildFromInventoryButton);
        }
    }
}
