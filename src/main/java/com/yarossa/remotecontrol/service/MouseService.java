package com.yarossa.remotecontrol.service;

import java.util.List;
import com.yarossa.remotecontrol.dto.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MouseService {

	private static final Logger log = LoggerFactory.getLogger(MouseService.class);

	private final SystemCommandService systemCommandService;

	public MouseService(SystemCommandService systemCommandService) {
		this.systemCommandService = systemCommandService;
	}

	public void click(String button) {
		String normalizedButton = normalizeButton(button);
		String xdotoolButton = toXdotoolButton(normalizedButton);

		log.info("Mouse click command: button={}", normalizedButton);
		executeXdotool(List.of("xdotool", "click", xdotoolButton));
	}

	public void down(String button) {
		String normalizedButton = normalizeButton(button);
		String xdotoolButton = toXdotoolButton(normalizedButton);

		log.info("Mouse down command: button={}", normalizedButton);
		executeXdotool(List.of("xdotool", "mousedown", xdotoolButton));
	}

	public void up(String button) {
		String normalizedButton = normalizeButton(button);
		String xdotoolButton = toXdotoolButton(normalizedButton);

		log.info("Mouse up command: button={}", normalizedButton);
		executeXdotool(List.of("xdotool", "mouseup", xdotoolButton));
	}

	public void move(int dx, int dy) {
		log.info("Mouse move command: dx={}, dy={}", dx, dy);
		executeXdotool(List.of("xdotool", "mousemove_relative", "--", String.valueOf(dx), String.valueOf(dy)));
	}

	public void scroll(int amount) {
		log.info("Mouse scroll command: amount={}", amount);
		if (amount == 0) {
			return;
		}

		String button = amount > 0 ? "4" : "5";
		for (int i = 0; i < Math.abs(amount); i++) {
			executeXdotool(List.of("xdotool", "click", button));
		}
	}

	private void executeXdotool(List<String> command) {
		CommandResult result = systemCommandService.execute(command);
		if (!result.success()) {
			throw new RuntimeException("xdotool command failed: " + result.output());
		}
	}

	private String normalizeButton(String button) {
		return button.toLowerCase();
	}

	private String toXdotoolButton(String normalizedButton) {
		return switch (normalizedButton) {
			case "left" -> "1";
			case "middle" -> "2";
			case "right" -> "3";
			default -> throw new IllegalArgumentException("Unsupported mouse button: " + normalizedButton);
		};
	}
}
