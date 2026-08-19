package com.yarossa.remotecontrol.dto;

import jakarta.validation.constraints.NotNull;

public record KeyboardTextRequest(@NotNull String text) {
}
