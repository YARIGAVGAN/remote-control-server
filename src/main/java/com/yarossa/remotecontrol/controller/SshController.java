package com.yarossa.remotecontrol.controller;

import com.yarossa.remotecontrol.dto.ServiceStatusResponse;
import com.yarossa.remotecontrol.service.LinuxServiceManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services/ssh")
public class SshController {

	private static final String SSH_SERVICE = "ssh";

	private final LinuxServiceManager linuxServiceManager;

	public SshController(LinuxServiceManager linuxServiceManager) {
		this.linuxServiceManager = linuxServiceManager;
	}

	@GetMapping("/status")
	public ServiceStatusResponse status() {
		return linuxServiceManager.getServiceStatus(SSH_SERVICE);
	}
}
