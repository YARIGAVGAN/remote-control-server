package com.yarossa.remotecontrol.service;

import com.yarossa.remotecontrol.dto.CommandResult;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PowerService {

	private static final Logger log = LoggerFactory.getLogger(PowerService.class);
	private static final List<List<String>> SUSPEND_COMMANDS = List.of(
			List.of("systemctl", "suspend"),
			List.of("systemctl", "-i", "suspend"),
			List.of("loginctl", "suspend"),
			List.of("sudo", "-S", "-p", "", "/bin/systemctl", "suspend"),
			List.of("sudo", "-S", "-p", "", "systemctl", "suspend")
	);

	private final SystemCommandService systemCommandService;

	public PowerService(SystemCommandService systemCommandService) {
		this.systemCommandService = systemCommandService;
	}

	public void suspend() {
		List<String> failures = new ArrayList<>();
		for (List<String> command : SUSPEND_COMMANDS) {
			try {
				log.info("System suspend command: {}", String.join(" ", command));
				CommandResult result = systemCommandService.execute(command);
				if (result.success()) {
					log.info("System suspend command accepted: {}", String.join(" ", command));
					return;
				}
				failures.add(String.join(" ", command) + " -> exit " + result.exitCode() + ": " + result.output());
			} catch (RuntimeException exception) {
				failures.add(String.join(" ", command) + " -> " + exception.getMessage());
			}
		}
		String message = "All suspend commands failed: " + String.join(" | ", failures);
		log.error(message);
		throw new IllegalStateException(message);
	}
}
