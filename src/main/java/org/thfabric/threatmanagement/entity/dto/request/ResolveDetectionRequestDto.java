package org.thfabric.threatmanagement.entity.dto.request;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResolveDetectionRequestDto(@NotNull UUID detectionUuid,
                                         @NotNull LocalDateTime resolveTime) {
}
