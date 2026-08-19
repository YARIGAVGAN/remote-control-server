package com.yarossa.remotecontrol.dto;

public record ApiResponse(boolean success, String message) {

	public static ApiResponse ok() {
		return new ApiResponse(true, "OK");
	}
}
