/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.driver.sdl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerBackendPolicyTest {
	private static final ControllerBackendPolicy.Device A = new ControllerBackendPolicy.Device(4, 0x413d, 0x2104, (byte) 'g');
	private static final ControllerBackendPolicy.Device B = new ControllerBackendPolicy.Device(5, 0x413d, 0x2104, (byte) 'g');
	private static final ControllerBackendPolicy.Device X = new ControllerBackendPolicy.Device(3, 0x413d, 0x2104, (byte) 'x');

	@Test
	void lateGameInputArrivalRemovesPreviouslyEnumeratedXInputAlias() {
		assertEquals(List.of(X), ControllerBackendPolicy.select(List.of(X)));
		assertEquals(List.of(A), ControllerBackendPolicy.select(List.of(X, A)));
	}

	@Test
	void duplicateAliasNeverSuppressesEitherPhysicalReceiver() {
		assertEquals(List.of(A, B), ControllerBackendPolicy.select(List.of(A, X, B)));
		assertEquals(List.of(B, A), ControllerBackendPolicy.select(List.of(X, B, A)));
	}

	@Test
	void unrelatedAndXInputOnlyDevicesRemainAvailable() {
		var otherX = new ControllerBackendPolicy.Device(9, 0x045e, 0x028e, (byte) 'x');
		var hid = new ControllerBackendPolicy.Device(10, 0x413d, 0x2104, (byte) 'h');
		assertEquals(List.of(A, otherX, hid), ControllerBackendPolicy.select(List.of(A, otherX, hid)));
		assertEquals(List.of(X), ControllerBackendPolicy.select(List.of(X)));
	}

	@Test
	void unknownVendorProductCannotProveDuplication() {
		var unknownGameInput = new ControllerBackendPolicy.Device(11, 0, 0, (byte) 'g');
		var unknownXInput = new ControllerBackendPolicy.Device(12, 0, 0, (byte) 'x');
		assertEquals(List.of(unknownXInput, unknownGameInput), ControllerBackendPolicy.select(List.of(unknownXInput, unknownGameInput)));
	}
}
