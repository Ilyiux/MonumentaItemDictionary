package dev.eliux.monumentaitemdictionary.mixin;

import dev.eliux.monumentaitemdictionary.Mid;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow @Final public DefaultedList<Slot> slots;
    @Inject(at = @At("HEAD"), method = "onClosed")
    private void onClosed(PlayerEntity player, CallbackInfo ci) {
        Mid.LOGGER.info(String.valueOf(slots.get(25).getStack().getTooltip(player, TooltipContext.BASIC).get(0).getSiblings().get(0).getString()));
        Mid.LOGGER.info(String.valueOf(slots.get(26).getStack().getTooltip(player, TooltipContext.BASIC).get(0).getSiblings().get(0).getString()));
        Mid.LOGGER.info(String.valueOf(slots.get(35).getStack().getTooltip(player, TooltipContext.BASIC).get(0).getSiblings().get(0).getString()));
        Mid.LOGGER.info(String.valueOf(slots.get(44).getStack().getTooltip(player, TooltipContext.BASIC).get(0).getSiblings().get(0).getString()));
        Mid.LOGGER.info(String.valueOf(slots.get(52).getStack().getTooltip(player, TooltipContext.BASIC).get(0).getSiblings().get(0).getString()));
        Mid.LOGGER.info(String.valueOf(slots.get(53).getStack().getTooltip(player, TooltipContext.BASIC).get(0).getSiblings().get(0).getString()));
    }
}
