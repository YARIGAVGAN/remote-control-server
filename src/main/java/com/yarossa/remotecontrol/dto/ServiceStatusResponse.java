package com.yarossa.remotecontrol.dto;

public record ServiceStatusResponse(String service, boolean active, String rawStatus) {
}
