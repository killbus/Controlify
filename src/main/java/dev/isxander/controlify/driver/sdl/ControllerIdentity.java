/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.driver.sdl;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

/** Native identities must identify a device, not its current connection slot. */
public final class ControllerIdentity {
	private ControllerIdentity() {
	}

	public static Optional<String> gameInputUid(byte driver, @Nullable String serial, @Nullable String path) {
		// Keep serial-based UIDs compatible across drivers. Other SDL paths may be slots
		// (XInput#N) or transient handles, so only accept GameInput's APP_LOCAL_DEVICE_ID.
		if (driver != 'g' || (serial != null && !serial.isBlank())
				|| path == null || !path.matches("[0-9a-fA-F]{64}") || path.equals("0".repeat(64))) {
			return Optional.empty();
		}
		return Optional.of("gameinput-" + path.toLowerCase(Locale.ROOT));
	}
}
