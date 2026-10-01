/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.controllermanager;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ControllerSelectionTest {
	@Test
	void missingManualDeviceDoesNotBorrowAnotherPlayerOnStartup() {
		assertEquals(Optional.empty(), ControllerSelection.resolve(List.of("B"), Function.identity(), null, false, "A"));
	}

	@Test
	void manualDisconnectWaitsForTheSelectedDevice() {
		assertEquals(Optional.empty(), ControllerSelection.afterDisconnect(List.of("B"), Function.identity(), null, false, "A"));
		assertFalse(ControllerSelection.acceptsConnection("B", null, false, "A"));
		assertTrue(ControllerSelection.acceptsConnection("A", null, false, "A"));
		assertEquals(Optional.of("A"), ControllerSelection.resolve(List.of("B", "A"), Function.identity(), null, false, "A"));
	}

	@Test
	void profileBindingOverridesTheGlobalPreferenceAndAutomaticMode() {
		assertEquals(Optional.empty(), ControllerSelection.resolve(List.of("B"), Function.identity(), "A", true, "B"));
		assertEquals(Optional.empty(), ControllerSelection.afterDisconnect(List.of("B"), Function.identity(), "A", true, "B"));
		assertFalse(ControllerSelection.acceptsConnection("B", "A", true, "B"));
		assertTrue(ControllerSelection.acceptsConnection("A", "A", true, "B"));
	}

	@Test
	void automaticModeKeepsFallbackAndHotplugSelection() {
		assertEquals(Optional.of("B"), ControllerSelection.afterDisconnect(List.of("B"), Function.identity(), null, true, "A"));
		assertEquals(Optional.of("B"), ControllerSelection.resolve(List.of("B"), Function.identity(), null, true, "A"));
		assertTrue(ControllerSelection.acceptsConnection("B", null, true, "A"));
	}

	@Test
	void blankPreferenceKeepsFirstDeviceFallbackWithoutHotplugStealing() {
		assertEquals(Optional.of("B"), ControllerSelection.resolve(List.of("B"), Function.identity(), null, false, ""));
		assertEquals(Optional.of("B"), ControllerSelection.afterDisconnect(List.of("B"), Function.identity(), null, false, ""));
		assertFalse(ControllerSelection.acceptsConnection("C", null, false, ""));
	}
}
