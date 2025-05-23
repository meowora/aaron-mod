package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Functions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> 
implements ScreenHandlerProvider<GenericContainerScreenHandler> {

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/GenericContainerScreen;drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V", ordinal = 0))
	private boolean aaronMod$hideScreenToolips(GenericContainerScreen container, DrawContext context, int mouseX, int mouseY) {
		return !(Functions.isOnHypixel() && AaronModConfigManager.get().skyblock.dungeons.hideClickOnTimeTooltips && this.title.getString().equals("Click the button on time!"));
	}
}
