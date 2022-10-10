package org.thfabric.threatmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_BASE_DETECTION")
@Data
@NoArgsConstructor
public class BaseDetectionEntity {
    @Id
    @Column(name = "DETECTION_UUID")
    private UUID id;
    private LocalDateTime timeReported;
    private LocalDateTime timeInserted;

    @ManyToOne
    @JoinColumn(name = "DEVICE_UUID", nullable = false)
    private DeviceEntity deviceEntity;
}
