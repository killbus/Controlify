/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.driver.sdl;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ControllerUidAllocatorTest {
	@Test
	void reconnectingBaseDeviceCannotOverwriteTheSurvivingDevice() {
		Set<String> connected = new HashSet<>(Set.of("U", "U-1"));
		connected.remove("U");
		String reconnected = ControllerUidAllocator.allocate("U", connected);
		assertFalse(connected.contains(reconnected), "Reconnection would overwrite another controller");
		assertEquals("U", reconnected);
		assertTrue(connected.add(reconnected));
		assertEquals(2, connected.size());
	}

	@Test
	void reusesAHoleRatherThanCollidingWithAHigherSuffix() {
		assertEquals("U-1", ControllerUidAllocator.allocate("U", Set.of("U", "U-2")));
	}

	@Test
	void preservesExistingUidFormatForInitialDiscovery() {
		assertEquals("U", ControllerUidAllocator.allocate("U", Set.of()));
		assertEquals("U-1", ControllerUidAllocator.allocate("U", Set.of("U")));
		assertEquals("U-2", ControllerUidAllocator.allocate("U", Set.of("U", "U-1")));
	}

	@Test
	void unrelatedPrefixDoesNotConsumeAnIdentity() {
		assertEquals("U", ControllerUidAllocator.allocate("U", Set.of("Unrelated")));
	}
}
