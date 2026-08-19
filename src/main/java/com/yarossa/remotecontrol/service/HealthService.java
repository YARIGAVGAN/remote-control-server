package com.yarossa.remotecontrol.service;

import com.yarossa.remotecontrol.dto.HealthResponse;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

	private static final String SERVER_NAME = "remote-control-server";

	private final Clock clock;

	public HealthService(Clock clock) {
		this.clock = clock;
	}

	public HealthResponse getHealth() {
		return new HealthResponse("ok", SERVER_NAME, clock.instant());
	}
}
