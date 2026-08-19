package com.yarossa.remotecontrol.controller;

import com.yarossa.remotecontrol.dto.ApiResponse;
import com.yarossa.remotecontrol.dto.MouseClickRequest;
import com.yarossa.remotecontrol.dto.MouseMoveRequest;
import com.yarossa.remotecontrol.dto.MouseScrollRequest;
import com.yarossa.remotecontrol.service.MouseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mouse")
public class MouseController {

	private final MouseService mouseService;

	public MouseController(MouseService mouseService) {
		this.mouseService = mouseService;
	}

	@PostMapping("/click")
	public ApiResponse click(@Valid @RequestBody MouseClickRequest request) {
		mouseService.click(request.button());
		return ApiResponse.ok();
	}

	@PostMapping("/down")
	public ApiResponse down(@Valid @RequestBody MouseClickRequest request) {
		mouseService.down(request.button());
		return ApiResponse.ok();
	}

	@PostMapping("/up")
	public ApiResponse up(@Valid @RequestBody MouseClickRequest request) {
		mouseService.up(request.button());
		return ApiResponse.ok();
	}

	@PostMapping("/move")
	public ApiResponse move(@Valid @RequestBody MouseMoveRequest request) {
		mouseService.move(request.dx(), request.dy());
		return ApiResponse.ok();
	}

	@PostMapping("/scroll")
	public ApiResponse scroll(@Valid @RequestBody MouseScrollRequest request) {
		mouseService.scroll(request.amount());
		return ApiResponse.ok();
	}
}
