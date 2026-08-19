package com.yarossa.remotecontrol.dto;

import jakarta.validation.constraints.NotBlank;

public record MouseClickRequest(@NotBlank String button) {
}
