package com.yarossa.remotecontrol.controller;

import com.yarossa.remotecontrol.dto.ApiResponse;
import com.yarossa.remotecontrol.dto.KeyboardKeyRequest;
import com.yarossa.remotecontrol.dto.KeyboardShortcutRequest;
import com.yarossa.remotecontrol.dto.KeyboardTextRequest;
import com.yarossa.remotecontrol.service.KeyboardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keyboard")
public class KeyboardController {

	private final KeyboardService keyboardService;

	public KeyboardController(KeyboardService keyboardService) {
		this.keyboardService = keyboardService;
	}

	@PostMapping("/text")
	public ApiResponse text(@Valid @RequestBody KeyboardTextRequest request) {
		keyboardService.typeText(request.text());
		return ApiResponse.ok();
	}

	@PostMapping("/key")
	public ApiResponse key(@Valid @RequestBody KeyboardKeyRequest request) {
		keyboardService.pressKey(request.key());
		return ApiResponse.ok();
	}

	@PostMapping("/shortcut")
	public ApiResponse shortcut(@Valid @RequestBody KeyboardShortcutRequest request) {
		keyboardService.pressShortcut(request.keys());
		return ApiResponse.ok();
	}
}
