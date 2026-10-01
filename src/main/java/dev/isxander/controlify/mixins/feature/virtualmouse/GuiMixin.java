/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.mixins.feature.virtualmouse;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(
		//? if >=26.2 {
		net.minecraft.client.gui.Gui.class
		//?} else {
		/*net.minecraft.client.Minecraft.class
		*///?}
)
public class GuiMixin {
	//? if >=26.3 {
	@WrapWithCondition(
			method = "setScreen",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;resyncMousePosition()V")
	)
	private boolean shouldResyncMousePosition(MouseHandler mouseHandler, Screen screen) {
		// Vanilla polls the desktop cursor before enabling the virtual mouse,
		// even when another controller instance owns the foreground window.
		// Preserve releaseMouse's local position or the previous menu position.
		// MIXED mode still receives physical mouse events and cursor handoffs.
		return screen == null || !Controlify.instance().currentInputMode().isController();
	}
	//?}

	@Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateTitle()V"))
	private void onScreenChanged(Screen screen, CallbackInfo ci) {
		Optional.ofNullable(Controlify.instance().virtualMouseHandler())
				.ifPresent(VirtualMouseHandler::onScreenChanged);
	}
}
