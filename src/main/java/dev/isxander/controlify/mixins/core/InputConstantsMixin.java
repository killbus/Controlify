/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.mixins.core;

import org.spongepowered.asm.mixin.Mixin;

//? if >=26.3 {
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDLMouse;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputConstants.class)
public class InputConstantsMixin {
	@Inject(method = "releaseMouse", at = @At("HEAD"))
	private static void ensureMouseVisible(CallbackInfo ci) {
		SDLMouse.SDL_ShowCursor();
	}

	// SDL_WarpMouseInWindow moves the desktop-wide cursor. Vanilla grab/release
	// always call it, so a background OOF-input instance would drag the shared
	// cursor into its own window (often pinned to its edge) whenever it grabbed
	// or released. Only the window that holds keyboard focus may move it.
	@Inject(method = { "grabMouse", "releaseMouse" }, at = @At("HEAD"), cancellable = true)
	private static void suppressBackgroundCursorWarp(Window window, double x, double y, CallbackInfo ci) {
		if (SDLKeyboard.SDL_GetKeyboardFocus() != window.handle()) {
			ci.cancel();
		}
	}
}
//?} else {
/*@Mixin(dev.isxander.controlify.utils.DummyMixinTarget.class)
public class InputConstantsMixin {
}
*///?}
