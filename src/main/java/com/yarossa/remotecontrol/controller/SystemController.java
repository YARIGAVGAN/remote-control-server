package com.yarossa.remotecontrol.controller;

import com.yarossa.remotecontrol.dto.ApiResponse;
import com.yarossa.remotecontrol.service.PowerService;
import com.yarossa.remotecontrol.service.VolumeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {

	private final VolumeService volumeService;
	private final PowerService powerService;

	public SystemController(VolumeService volumeService, PowerService powerService) {
		this.volumeService = volumeService;
		this.powerService = powerService;
	}

	@PostMapping("/volume/up")
	public ApiResponse volumeUp() {
		volumeService.volumeUp();
		return ApiResponse.ok();
	}

	@PostMapping("/volume/down")
	public ApiResponse volumeDown() {
		volumeService.volumeDown();
		return ApiResponse.ok();
	}

	@PostMapping("/sleep")
	public ApiResponse sleep() {
		powerService.suspend();
		return ApiResponse.ok();
	}
}
