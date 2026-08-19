package com.yarossa.remotecontrol.dto;

public record CommandResult(int exitCode, String output) {

	public boolean success() {
		return exitCode == 0;
	}
}
