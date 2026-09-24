/*
 * Copyright (C) 2026 isXander
 * This file is part of Controlify.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.isxander.controlify.compatibility.yacl.screenop;

import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.compatibility.yacl.mixins.GroupSeparatorEntryAccessor;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ComponentProcessor;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.yacl3.gui.OptionListWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OptionListWidgetComponentProcessor implements ComponentProcessor {
	private final OptionListWidget optionListWidget;

	public OptionListWidgetComponentProcessor(OptionListWidget optionListWidget) {
		this.optionListWidget = optionListWidget;
	}

	/**
	 * Allows navigating between groups using secondary navigation up/down.
	 */
	@Override
	public boolean overrideControllerNavigation(ScreenProcessor<?> screen, ControllerEntity controller) {
		boolean up = ControlifyBindings.GUI_SECONDARY_NAVI_UP.on(controller).justPressed();
		boolean down = ControlifyBindings.GUI_SECONDARY_NAVI_DOWN.on(controller).justPressed();
		if (up || down) {
			OptionListWidget.GroupSeparatorEntry nextGroup = findNextGroup(optionListWidget, up ? -1 : 1);
			if (nextGroup != null) {
				if (nextGroup.isExpanded()) {
					// focus the first child that has focusable buttons, or the child entry
					// itself if it has none - e.g. an empty list group's EmptyListLabel
					var childEntry = findFirstFocusableChild(nextGroup);
					if (childEntry != null) {
						var buttonsWithinChild = childEntry.children();
						optionListWidget.setFocused(childEntry);
						if (!buttonsWithinChild.isEmpty()) {
							childEntry.setFocused(buttonsWithinChild.get(0));
						}
					} else {
						optionListWidget.setFocused(nextGroup);
					}
				} else {
					optionListWidget.setFocused(nextGroup);
				}
				return true;
			}
		}

		return false;
	}

	private static @Nullable OptionListWidget.Entry findFirstFocusableChild(OptionListWidget.GroupSeparatorEntry group) {
		List<OptionListWidget.Entry> childEntries = ((GroupSeparatorEntryAccessor) group).getChildEntries();
		for (OptionListWidget.Entry child : childEntries) {
			if (!child.children().isEmpty()) {
				return child;
			}
		}
		return childEntries.isEmpty() ? null : childEntries.get(0);
	}

	private static @Nullable OptionListWidget.GroupSeparatorEntry findNextGroup(OptionListWidget optionList, int direction) {
		List<OptionListWidget.Entry> entries = optionList.children();
		OptionListWidget.Entry focusedEntry = optionList.getFocused();
		int currentIndex = entries.indexOf(focusedEntry);
		if (direction < 0) currentIndex -= 2;
		if (currentIndex < 0) currentIndex = 0;

		for (int i = currentIndex; i >= 0 && i < entries.size(); i += direction) {
			OptionListWidget.Entry entry = entries.get(i);
			if (entry instanceof OptionListWidget.GroupSeparatorEntry groupEntry) {
				return groupEntry;
			}
		}

		return null;
	}
}
