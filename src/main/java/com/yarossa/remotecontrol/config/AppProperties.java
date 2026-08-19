package com.yarossa.remotecontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String apiKey, Vnc vnc, Ir ir) {

	public record Vnc(String serviceName) {
	}

	public record Ir(String defaultRemote) {
	}
}
