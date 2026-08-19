package com.yarossa.remotecontrol.service;

import com.yarossa.remotecontrol.dto.CommandResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class SystemCommandService {

	private static final String SUDO_PASSWORD = "1";

	public CommandResult execute(List<String> command) {
		if (command == null || command.isEmpty()) {
			throw new IllegalArgumentException("Command must not be empty");
		}

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		try {
			Process process = processBuilder.start();
			writeSudoPasswordIfNeeded(command, process);
			boolean finished = process.waitFor(10, TimeUnit.SECONDS);
			if (!finished) {
				process.destroyForcibly();
				throw new RuntimeException("Command timed out: " + command.get(0));
			}

			byte[] outputBytes = process.getInputStream().readAllBytes();
			String output = new String(outputBytes, StandardCharsets.UTF_8).trim();
			return new CommandResult(process.exitValue(), output);
		} catch (IOException exception) {
			throw new RuntimeException("Failed to start command '" + command.get(0) + "'. Is it installed and available in PATH?", exception);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Command interrupted: " + command.get(0), exception);
		}
	}

	private void writeSudoPasswordIfNeeded(List<String> command, Process process) throws IOException {
		if (!"sudo".equals(command.get(0))) {
			process.getOutputStream().close();
			return;
		}

		try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
			writer.write(SUDO_PASSWORD);
			writer.write('\n');
			writer.flush();
		}
	}
}
