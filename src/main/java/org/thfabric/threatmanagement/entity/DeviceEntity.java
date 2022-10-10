package org.thfabric.threatmanagement.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.thfabric.threatmanagement.entity.enums.DeviceType;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Table(name = "T_DEVICE")
@Entity
@Data
@NoArgsConstructor
public class DeviceEntity {
    @Id
    @Column(name = "DEVICE_UUID", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    private String deviceModel;
    private String osVersion;

    @OneToMany(mappedBy = "deviceEntity")
    private List<DetectionEntity> detectionEntities;

    @OneToMany(mappedBy = "deviceEntity")
    private List<BaseDetectionEntity> baseDetectionEntities;


}
