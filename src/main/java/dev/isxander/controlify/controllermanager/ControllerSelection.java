/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.controllermanager;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Selection rules shared by configuration changes and device lifecycle events. */
public final class ControllerSelection {
	private ControllerSelection() {
	}

	public static <T> Optional<T> resolve(List<T> connected, Function<T, String> uid,
			@Nullable String profileUid, boolean automatic, String preferredUid) {
		if (profileUid != null) {
			return connected.stream().filter(c -> profileUid.equals(uid.apply(c))).findFirst();
		}
		if (!automatic && !preferredUid.isEmpty()) {
			// An explicit manual choice is a binding, not a fallback preference.
			return connected.stream().filter(c -> preferredUid.equals(uid.apply(c))).findFirst();
		}
		return connected.stream().findFirst();
	}

	public static <T> Optional<T> afterDisconnect(List<T> connected, Function<T, String> uid,
			@Nullable String profileUid, boolean automatic, String preferredUid) {
		return resolve(connected, uid, profileUid, automatic, preferredUid);
	}

	public static boolean acceptsConnection(String uid, @Nullable String profileUid,
			boolean automatic, String preferredUid) {
		return profileUid == null ? automatic || uid.equals(preferredUid) : profileUid.equals(uid);
	}
}
