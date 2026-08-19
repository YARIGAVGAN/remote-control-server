package com.yarossa.remotecontrol.controller;

import com.yarossa.remotecontrol.config.AppProperties;
import com.yarossa.remotecontrol.dto.ApiResponse;
import com.yarossa.remotecontrol.dto.ServiceStatusResponse;
import com.yarossa.remotecontrol.service.LinuxServiceManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services/vnc")
public class RemoteDesktopController {

	private final LinuxServiceManager linuxServiceManager;
	private final AppProperties appProperties;

	public RemoteDesktopController(LinuxServiceManager linuxServiceManager, AppProperties appProperties) {
		this.linuxServiceManager = linuxServiceManager;
		this.appProperties = appProperties;
	}

	@PostMapping("/start")
	public ApiResponse start() {
		linuxServiceManager.startService(vncServiceName());
		return ApiResponse.ok();
	}

	@PostMapping("/stop")
	public ApiResponse stop() {
		linuxServiceManager.stopService(vncServiceName());
		return ApiResponse.ok();
	}

	@GetMapping("/status")
	public ServiceStatusResponse status() {
		return linuxServiceManager.getServiceStatus(vncServiceName());
	}

	private String vncServiceName() {
		return appProperties.vnc().serviceName();
	}
}
