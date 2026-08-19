package com.yarossa.remotecontrol.service;

import com.yarossa.remotecontrol.dto.CommandResult;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VolumeService {

	private static final Logger log = LoggerFactory.getLogger(VolumeService.class);
	private static final String STEP = "5%";

	private final SystemCommandService systemCommandService;

	public VolumeService(SystemCommandService systemCommandService) {
		this.systemCommandService = systemCommandService;
	}

	public void volumeUp() {
		log.info("System volume up");
		executeFirstSuccessful(
				List.of("wpctl", "set-volume", "@DEFAULT_AUDIO_SINK@", STEP + "+"),
				List.of("pactl", "set-sink-volume", "@DEFAULT_SINK@", "+" + STEP),
				List.of("amixer", "-D", "pulse", "sset", "Master", STEP + "+"),
				List.of("xdotool", "key", "XF86AudioRaiseVolume"),
				List.of("bash", "-lc", "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR:-/run/user/$(id -u)} wpctl set-volume @DEFAULT_AUDIO_SINK@ " + STEP + "+"),
				List.of("bash", "-lc", "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR:-/run/user/$(id -u)} pactl set-sink-volume @DEFAULT_SINK@ +" + STEP)
		);
	}

	public void volumeDown() {
		log.info("System volume down");
		executeFirstSuccessful(
				List.of("wpctl", "set-volume", "@DEFAULT_AUDIO_SINK@", STEP + "-"),
				List.of("pactl", "set-sink-volume", "@DEFAULT_SINK@", "-" + STEP),
				List.of("amixer", "-D", "pulse", "sset", "Master", STEP + "-"),
				List.of("xdotool", "key", "XF86AudioLowerVolume"),
				List.of("bash", "-lc", "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR:-/run/user/$(id -u)} wpctl set-volume @DEFAULT_AUDIO_SINK@ " + STEP + "-"),
				List.of("bash", "-lc", "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR:-/run/user/$(id -u)} pactl set-sink-volume @DEFAULT_SINK@ -" + STEP)
		);
	}

	@SafeVarargs
	private final void executeFirstSuccessful(List<String>... commands) {
		List<String> failures = new ArrayList<>();
		for (List<String> command : commands) {
			try {
				CommandResult result = systemCommandService.execute(command);
				if (result.success()) {
					return;
				}
				failures.add(String.join(" ", command) + " -> exit " + result.exitCode() + ": " + result.output());
			} catch (RuntimeException exception) {
				failures.add(String.join(" ", command) + " -> " + exception.getMessage());
			}
		}

		throw new RuntimeException("Volume command failed: " + String.join(" | ", failures));
	}
}
