package com.yarossa.remotecontrol.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record KeyboardShortcutRequest(@NotEmpty List<String> keys) {
}
