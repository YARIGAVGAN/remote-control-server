package com.yarossa.remotecontrol.dto;

import jakarta.validation.constraints.NotBlank;

public record KeyboardKeyRequest(@NotBlank String key) {
}
