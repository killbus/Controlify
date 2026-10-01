# Manual controller identity on Windows

For multiple identical wireless controllers, Controlify uses the native GameInput device ID when a GameInput controller does not provide a serial number. Turning controllers off and on in a different order then preserves their UIDs. An absent manually selected controller stays unselected until that device returns.

After upgrading from order-based UIDs, reconnect the controllers and select the intended device again in global settings (and in any profile with a controller binding). Old device settings and profile overrides remain in the configuration, but are not automatically assigned to the new UIDs: the old records cannot tell which physical device they belonged to. Reapply any device-specific settings to the newly identified devices as needed.

Keep receivers in their existing USB ports. Microsoft's GameInput contract guarantees the application-local device ID across application runs and system reboots for the same device and USB port. Moving ports or changing application identity may require selecting the device again. A receiver that exposes different physical handsets as a single logical device cannot distinguish those handsets this way.

Controlify also reapplies SDL's native GameInput-over-XInput preference after connection changes. This handles an XInput alias enumerated before its GameInput device arrives. Both physical GameInput controllers remain available; XInput-only models are kept. XInput slot names such as XInput#0 do not identify physical devices. Controllers without a serial number or a usable GameInput identity retain the legacy UID behavior and are not covered by this guarantee.

## Evidence and scope

With two 413d:2104 receivers left plugged in, labelled button presses before and after reversing the power-on order showed unchanged GameInput paths and new SDL instance IDs. The additional XInput slot changed from physical controller A to B. This was captured in a separate SDL 3.4.12 probe; it does not establish the exact SDL library loaded by a running Minecraft instance.

- [Microsoft GameInputDeviceInfo: deviceId persistence](https://learn.microsoft.com/en-us/gaming/gdk/docs/reference/input/gameinput/structs/gameinputdeviceinfo#remarks)
- [SDL GameInput path generation and device precedence](https://github.com/libsdl-org/SDL/blob/release-3.4.12/src/joystick/gdk/SDL_gameinputjoystick.cpp)
- [SDL XInput addition and slot-based paths](https://github.com/libsdl-org/SDL/blob/release-3.4.12/src/joystick/windows/SDL_xinputjoystick.c)
