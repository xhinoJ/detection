package org.thfabric.threatmanagement.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thfabric.threatmanagement.entity.BaseDetectionEntity;
import org.thfabric.threatmanagement.entity.DeviceEntity;
import org.thfabric.threatmanagement.entity.dto.request.PeriodicalSavingDetectionRequestDto;
import org.thfabric.threatmanagement.exception.WrongParameterException;
import org.thfabric.threatmanagement.repository.BaseDetectionRepository;
import org.thfabric.threatmanagement.repository.DeviceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class BaseDetectionService extends DetectionQuerySpecificationService {

    public static final String DEVICE_IS_NOT_PRESENT = "This device is not present in our system!";

    private final BaseDetectionRepository baseDetectionRepository;
    private final DeviceRepository deviceRepository;

    /**
     * Method used to save periodical checks made on a device. These are data mostly only for statistics
     * It throws an exception in case the deviceId does not exist.
     *
     * @param requestDto @{@link PeriodicalSavingDetectionRequestDto}
     */
    public void savePeriodicalDetection(PeriodicalSavingDetectionRequestDto requestDto) {

        Optional<DeviceEntity> deviceEntity = deviceRepository.findById(requestDto.deviceId());
        if (deviceEntity.isEmpty()) throw new WrongParameterException(DEVICE_IS_NOT_PRESENT);

        BaseDetectionEntity detectionEntity = new BaseDetectionEntity();
        detectionEntity.setId(UUID.randomUUID());
        detectionEntity.setTimeReported(requestDto.time());
        detectionEntity.setDeviceEntity(deviceEntity.get());
        detectionEntity.setTimeInserted(LocalDateTime.now());
        baseDetectionRepository.save(detectionEntity);
    }

    /**
     * Method used to return all the BaseDeviceEntity based on the mentioned filters
     *
     * @param deviceIds         A {@link List} of device ids
     * @param startReportedDate Date reported starting date
     * @param endReportedDate   Date reported end date
     * @return A @{@link List } of {@link BaseDetectionEntity}
     */
    public List<BaseDetectionEntity> getBaseDetectionEntitiesWithParameters(List<UUID> deviceIds, LocalDateTime startReportedDate, LocalDateTime endReportedDate) {
        Specification<BaseDetectionEntity> entitySpecification = getBaseDetectionQuery(deviceIds, startReportedDate, endReportedDate);
        return baseDetectionRepository.findAll(entitySpecification);
    }

}
