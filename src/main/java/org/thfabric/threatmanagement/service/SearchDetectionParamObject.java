package org.thfabric.threatmanagement.service;

import org.thfabric.threatmanagement.entity.enums.DetectionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SearchDetectionParamObject(List<UUID> deviceIds, List<DetectionStatus> detectionStatuses,
                                         LocalDateTime startingDetectionDate, LocalDateTime endDetectionDate,
                                         List<String> applicationName, List<String> applicationType) {
}
