package com.yarossa.remotecontrol.dto;

import java.time.Instant;

public record HealthResponse(String status, String server, Instant timestamp) {
}
