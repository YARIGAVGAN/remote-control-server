package com.yarossa.remotecontrol.service;

import com.yarossa.remotecontrol.dto.CommandResult;
import com.yarossa.remotecontrol.dto.ServiceStatusResponse;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LinuxServiceManager {

	private static final Logger log = LoggerFactory.getLogger(LinuxServiceManager.class);
	private static final Set<String> ALLOWED_SERVICES = Set.of("x11vnc", "ssh", "lircd");

	private final SystemCommandService systemCommandService;

	public LinuxServiceManager(SystemCommandService systemCommandService) {
		this.systemCommandService = systemCommandService;
	}

	public void startService(String serviceName) {
		executeServiceCommand("start", serviceName);
	}

	public void stopService(String serviceName) {
		executeServiceCommand("stop", serviceName);
	}

	public void restartService(String serviceName) {
		executeServiceCommand("restart", serviceName);
	}

	public ServiceStatusResponse getServiceStatus(String serviceName) {
		String allowedServiceName = validateServiceName(serviceName);
		log.info("Linux service status command: service={}", allowedServiceName);
		CommandResult result = systemCommandService.execute(List.of("systemctl", "is-active", allowedServiceName));
		String rawStatus = result.output().isBlank() ? "unknown" : result.output();
		return new ServiceStatusResponse(allowedServiceName, "active".equals(rawStatus), rawStatus);
	}

	public boolean isActive(String serviceName) {
		return getServiceStatus(serviceName).active();
	}

	private void executeServiceCommand(String action, String serviceName) {
		String allowedServiceName = validateServiceName(serviceName);
		log.info("Linux service command: action={}, service={}", action, allowedServiceName);
		CommandResult result = systemCommandService.execute(List.of("systemctl", action, allowedServiceName));
		if (result.success()) {
			return;
		}

		CommandResult sudoResult = systemCommandService.execute(List.of("sudo", "-S", "-p", "", "systemctl", action, allowedServiceName));
		if (!sudoResult.success()) {
			throw new RuntimeException(
					"systemctl " + action + " failed for " + allowedServiceName +
							": " + result.output() +
							". sudo fallback: " + sudoResult.output()
			);
		}
	}

	private String validateServiceName(String serviceName) {
		if (serviceName == null || serviceName.isBlank()) {
			throw new IllegalArgumentException("Service name must not be blank");
		}

		String normalizedServiceName = serviceName.trim();
		if (!ALLOWED_SERVICES.contains(normalizedServiceName)) {
			throw new IllegalArgumentException("Unsupported service: " + serviceName);
		}

		return normalizedServiceName;
	}
}
