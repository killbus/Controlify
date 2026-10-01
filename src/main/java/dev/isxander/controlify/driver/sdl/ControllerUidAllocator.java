/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.driver.sdl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Assigns a free suffix without changing the device's base identity.
 * This prevents live UID collisions; identical devices without stable hardware
 * identifiers can still exchange slots when both disconnect and reconnect.
 */
public final class ControllerUidAllocator {
	private ControllerUidAllocator() {
	}

	public static String allocate(String baseUid, Collection<String> connectedUids) {
		Set<String> occupied = new HashSet<>(connectedUids);
		// Counts are not free slots: U-1 may remain connected after U disconnects.
		String candidate = baseUid;
		for (int suffix = 1; occupied.contains(candidate); suffix++) {
			candidate = baseUid + "-" + suffix;
		}
		return candidate;
	}
}
