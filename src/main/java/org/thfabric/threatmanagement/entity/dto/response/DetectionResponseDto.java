package org.thfabric.threatmanagement.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thfabric.threatmanagement.entity.BaseDetectionEntity;
import org.thfabric.threatmanagement.entity.DetectionEntity;
import org.thfabric.threatmanagement.entity.enums.DetectionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectionResponseDto {
    private UUID deviceId;
    private UUID detectionUuid;
    private LocalDateTime timeRetrieved;
    private LocalDateTime timeResolved;
    private String applicationName;
    private String applicationType;
    private DetectionStatus status;

    public DetectionResponseDto(BaseDetectionEntity detectionEntity) {
        timeRetrieved = detectionEntity.getTimeReported();
        detectionUuid = detectionEntity.getId();
        deviceId = detectionEntity.getDeviceEntity().getId();
        status = DetectionStatus.IGNORED;
    }

    public DetectionResponseDto(DetectionEntity detectionEntity) {
        timeRetrieved = detectionEntity.getTimeReported();
        detectionUuid = detectionEntity.getId();
        deviceId = detectionEntity.getDeviceEntity().getId();
        status = detectionEntity.getStatus();
        applicationName = detectionEntity.getAppName();
        applicationName = detectionEntity.getAppType();
        timeResolved = detectionEntity.getTimeResolved();

    }

}
