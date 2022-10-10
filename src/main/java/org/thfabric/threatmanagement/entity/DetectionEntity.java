package org.thfabric.threatmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thfabric.threatmanagement.entity.dto.request.SavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.enums.DetectionStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode
@Entity
@Table(name = "T_DETECTION")
@Data
@NoArgsConstructor
public class DetectionEntity {
    @Id
    @Column(name = "DETECTION_UUID")
    private UUID id;
    private LocalDateTime timeReported;
    private LocalDateTime timeInserted;

    @ManyToOne
    @JoinColumn(name = "DEVICE_UUID", nullable = false)
    private DeviceEntity deviceEntity;

    private LocalDateTime timeResolved;

    @Enumerated(EnumType.STRING)
    private DetectionStatus status;
    private String appName;
    private String appType;


    public DetectionEntity(SavingDetectionRequestDto requestDto, DeviceEntity deviceEntity) {
        this.setId(requestDto.detectionUuid());
        this.setTimeReported(requestDto.time());
        this.setDeviceEntity(deviceEntity);
        this.setTimeInserted(LocalDateTime.now());
        this.setAppName(requestDto.applicationName());
        this.setAppType(requestDto.applicationType());
        this.setStatus(DetectionStatus.NEW);
    }

}
