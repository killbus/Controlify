/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.driver.sdl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Applies SDL's GameInput preference to a complete enumeration, including hotplug. */
public final class ControllerBackendPolicy {
	private ControllerBackendPolicy() {
	}

	public record Device(int id, int vendor, int product, byte driver) {
		private long model() {
			return ((long) vendor << 32) | Integer.toUnsignedLong(product);
		}
	}

	public static List<Device> select(List<Device> devices) {
		// SDL uses VID/PID to prefer GameInput over XInput. An XInput device added
		// before GameInput discovers it can escape that check and survive hotplug.
		Set<Long> gameInputModels = devices.stream()
				.filter(d -> d.driver == 'g' && d.vendor != 0 && d.product != 0)
				.map(Device::model).collect(Collectors.toSet());
		return devices.stream()
				.filter(d -> d.driver != 'x' || !gameInputModels.contains(d.model()))
				.toList();
	}
}
