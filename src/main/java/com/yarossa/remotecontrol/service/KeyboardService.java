package com.yarossa.remotecontrol.service;

import com.yarossa.remotecontrol.dto.CommandResult;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KeyboardService {

	private static final Logger log = LoggerFactory.getLogger(KeyboardService.class);

	private static final Map<String, String> KEY_MAPPING = Map.ofEntries(
			Map.entry("ENTER", "Return"),
			Map.entry("ESCAPE", "Escape"),
			Map.entry("BACKSPACE", "BackSpace"),
			Map.entry("TAB", "Tab"),
			Map.entry("SPACE", "space"),
			Map.entry("UP", "Up"),
			Map.entry("DOWN", "Down"),
			Map.entry("LEFT", "Left"),
			Map.entry("RIGHT", "Right"),
			Map.entry("HOME", "Home"),
			Map.entry("END", "End"),
			Map.entry("PAGE_UP", "Page_Up"),
			Map.entry("PAGE_DOWN", "Page_Down"),
			Map.entry("DELETE", "Delete")
	);

	private static final Set<String> SHORTCUT_MODIFIERS = Set.of("CTRL", "ALT", "SHIFT", "SUPER");
	private static final Map<String, String> SHORTCUT_MODIFIER_MAPPING = Map.of(
			"CTRL", "ctrl",
			"ALT", "alt",
			"SHIFT", "shift",
			"SUPER", "super"
	);

	private final SystemCommandService systemCommandService;

	public KeyboardService(SystemCommandService systemCommandService) {
		this.systemCommandService = systemCommandService;
	}

	public void typeText(String text) {
		if (text == null || text.isEmpty()) {
			return;
		}

		log.info("Typing text length={}", text.length());
		executeXdotool(List.of("xdotool", "type", "--delay", "20", text));
	}

	public void pressKey(String key) {
		String xdotoolKey = mapKey(key);
		log.info("Keyboard key command: key={}", normalize(key));
		executeXdotool(List.of("xdotool", "key", xdotoolKey));
	}

	public void pressShortcut(List<String> keys) {
		if (keys == null || keys.isEmpty()) {
			throw new IllegalArgumentException("Shortcut keys must not be empty");
		}

		String shortcut = keys.stream()
				.map(this::mapShortcutKey)
				.collect(Collectors.joining("+"));

		log.info("Keyboard shortcut command: keys={}", keys.stream().map(this::normalize).toList());
		executeXdotool(List.of("xdotool", "key", shortcut));
	}

	private String mapKey(String key) {
		String normalized = normalize(key);
		String xdotoolKey = KEY_MAPPING.get(normalized);
		if (xdotoolKey == null) {
			throw new IllegalArgumentException("Unsupported keyboard key: " + key);
		}

		return xdotoolKey;
	}

	private String mapShortcutKey(String key) {
		String normalized = normalize(key);
		if (SHORTCUT_MODIFIERS.contains(normalized)) {
			return SHORTCUT_MODIFIER_MAPPING.get(normalized);
		}
		if (normalized.length() == 1 && Character.isLetterOrDigit(normalized.charAt(0))) {
			return normalized.toLowerCase(Locale.ROOT);
		}

		throw new IllegalArgumentException("Unsupported shortcut key: " + key);
	}

	private String normalize(String key) {
		if (key == null || key.isBlank()) {
			throw new IllegalArgumentException("Keyboard key must not be blank");
		}

		return key.trim().toUpperCase(Locale.ROOT);
	}

	private void executeXdotool(List<String> command) {
		CommandResult result = systemCommandService.execute(command);
		if (!result.success()) {
			throw new RuntimeException("xdotool command failed: " + result.output());
		}
	}
}
