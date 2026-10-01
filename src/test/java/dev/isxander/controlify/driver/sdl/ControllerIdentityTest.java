/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.driver.sdl;

import dev.isxander.controlify.controllermanager.ControllerSelection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ControllerIdentityTest {
	// Captured from two physical 413d:2104 receivers, then verified with labelled button presses.
	private static final String A = "02C32C56F14D77AD4950548C0A4616D7A8F0E708E2902147D664577A088D3BBA";
	private static final String B = "024DEA4A9CDF817F106936CA39D28BCE24A1AB79F60F2B2F30DADE330210E4B4";

	private static String connect(String path, List<String> connected) {
		return ControllerIdentity.gameInputUid((byte) 'g', null, path)
				.orElseGet(() -> ControllerUidAllocator.allocate("legacy-model", connected));
	}

	@Test
	void reversedPowerOnOrderKeepsBothPhysicalBindings() {
		String firstA = connect(A, List.of());
		String firstB = connect(B, List.of(firstA));
		String secondB = connect(B, List.of());
		String secondA = connect(A, List.of(secondB));
		assertEquals(firstA, secondA, "A must retain its identity when B powers on first");
		assertEquals(firstB, secondB, "B must retain its identity when it powers on first");
		assertNotEquals(secondA, secondB);
	}

	@Test
	void otherPhysicalDeviceCannotSatisfyManualOrProfileBinding() {
		String selectedA = connect(A, List.of());
		String onlyB = connect(B, List.of());
		assertEquals(Optional.empty(), ControllerSelection.resolve(List.of(onlyB), Function.identity(), null, false, selectedA));
		assertEquals(Optional.empty(), ControllerSelection.afterDisconnect(List.of(onlyB), Function.identity(), selectedA, true, ""));
		assertFalse(ControllerSelection.acceptsConnection(onlyB, null, false, selectedA));
		String reconnectedA = connect(A, List.of(onlyB));
		assertTrue(ControllerSelection.acceptsConnection(reconnectedA, null, false, selectedA));
	}

	@Test
	void nativeIdentityIsIndependentOfOtherConnectedDevicesAndHexCase() {
		assertEquals(connect(A, List.of()), connect(A, List.of("legacy-model", "legacy-model-1")));
		assertEquals(connect(A, List.of()), connect(A.toLowerCase(java.util.Locale.ROOT), List.of()));
		assertTrue(ControllerIdentity.gameInputUid((byte) 'g', "", A).isPresent());
	}

	@Test
	void existingSerialIdentitiesAndOtherBackendsKeepTheirLegacyUid() {
		assertTrue(ControllerIdentity.gameInputUid((byte) 'g', "real-serial", A).isEmpty());
		assertTrue(ControllerIdentity.gameInputUid((byte) 'h', null, A).isEmpty());
		assertTrue(ControllerIdentity.gameInputUid((byte) 'x', null, "XInput#0").isEmpty());
	}

	@Test
	void missingOrInvalidNativeIdentityDoesNotBecomeASharedUid() {
		for (String path : new String[]{null, "", "XInput#0", "not-a-device", "0".repeat(64)}) {
			assertTrue(ControllerIdentity.gameInputUid((byte) 'g', null, path).isEmpty());
		}
	}
}
