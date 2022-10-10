package org.thfabric.threatmanagement.entity.dto.request;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record SavingDetectionRequestDto(@NotNull UUID detectionUuid, @NotNull LocalDateTime time,
                                        @NotNull String applicationName,
                                        @NotNull String applicationType, @NotNull UUID deviceId) {
}
