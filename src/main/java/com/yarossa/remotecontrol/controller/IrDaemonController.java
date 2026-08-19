package com.yarossa.remotecontrol.controller;

import com.yarossa.remotecontrol.dto.ApiResponse;
import com.yarossa.remotecontrol.dto.ServiceStatusResponse;
import com.yarossa.remotecontrol.service.LinuxServiceManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services/lirc")
public class IrDaemonController {

	private static final String LIRC_SERVICE = "lircd";

	private final LinuxServiceManager linuxServiceManager;

	public IrDaemonController(LinuxServiceManager linuxServiceManager) {
		this.linuxServiceManager = linuxServiceManager;
	}

	@GetMapping("/status")
	public ServiceStatusResponse status() {
		return linuxServiceManager.getServiceStatus(LIRC_SERVICE);
	}

	@PostMapping("/restart")
	public ApiResponse restart() {
		linuxServiceManager.restartService(LIRC_SERVICE);
		return ApiResponse.ok();
	}
}
