package org.thfabric.threatmanagement.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thfabric.threatmanagement.entity.DetectionEntity;
import org.thfabric.threatmanagement.entity.DeviceEntity;
import org.thfabric.threatmanagement.entity.dto.request.ResolveDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.request.SavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.response.DetectionResponseDto;
import org.thfabric.threatmanagement.entity.enums.DetectionStatus;
import org.thfabric.threatmanagement.exception.WrongParameterException;
import org.thfabric.threatmanagement.repository.DetectionRepository;
import org.thfabric.threatmanagement.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class DetectionService extends DetectionQuerySpecificationService {

    public static final String DEVICE_IS_NOT_PRESENT = "This device is not present in our system!";
    public static final String THREAT_ALREADY_EXIST = "This threat notice cannot be created as it already exist in our system!";
    public static final String THREAT_IS_NOT_PRESENT = "This threat notice cannot be created as it already exist in our system!";
    public static final String THREAT_IS_ALREADY_RESOLVED = "This threat is already resolved in our system!";

    private final DetectionRepository detectionRepository;
    private final DeviceRepository deviceRepository;
    private final BaseDetectionService baseDetectionService;

    /**
     * Method used to save the DeviceEntity into the database
     * A conversion from DTO to DeviceEntity happens here
     * <p>
     * It throws an error in case the device does not exist or detectionId already exist
     *
     * @param requestDto @{@link SavingDetectionRequestDto}
     */
    public void saveNewDetection(SavingDetectionRequestDto requestDto) {

        Optional<DeviceEntity> deviceEntity = deviceRepository.findById(requestDto.deviceId());
        if (deviceEntity.isEmpty()) throw new WrongParameterException(DEVICE_IS_NOT_PRESENT);

        Optional<DetectionEntity> alreadyExistingDetectionEntity = detectionRepository.findById(requestDto.detectionUuid());
        if (alreadyExistingDetectionEntity.isPresent()) throw new WrongParameterException(THREAT_ALREADY_EXIST);

        DetectionEntity detectionEntity = new DetectionEntity(requestDto, deviceEntity.get());
        detectionRepository.save(detectionEntity);
    }

    /**
     * Method used to set a detection to status resolved.
     * It thrown an error in case the detection is already resolved or it does not exist
     *
     * @param requestDto @{@link ResolveDetectionRequestDto}
     */
    public void resolveDetection(ResolveDetectionRequestDto requestDto) {

        Optional<DetectionEntity> existingDetectionEntity = detectionRepository.findById(requestDto.detectionUuid());
        if (existingDetectionEntity.isEmpty()) throw new WrongParameterException(THREAT_IS_NOT_PRESENT);
        if (existingDetectionEntity.get().getStatus().equals(DetectionStatus.RESOLVED))
            throw new WrongParameterException(THREAT_IS_ALREADY_RESOLVED);

        existingDetectionEntity.get().setStatus(DetectionStatus.RESOLVED);
        existingDetectionEntity.get().setTimeResolved(requestDto.resolveTime());
        detectionRepository.save(existingDetectionEntity.get());
    }

    /**
     * Method used to get a list of {@link DetectionEntity} and {@link org.thfabric.threatmanagement.entity.BaseDetectionEntity}
     * and aggregates them together when the criteria are met and return them by the newest detection.
     *
     * @param searchDetectionParamObject @{@link SearchDetectionParamObject} List of query criteria
     * @return org.thfabric.threatmanagement.entity.dto.response.DetectionResponseDto
     */
    public List<DetectionResponseDto> getDetectionsByParameters(SearchDetectionParamObject searchDetectionParamObject) {
        List<DetectionResponseDto> detectionResponseDtos = new ArrayList<>();
        if (CollectionUtils.isEmpty(searchDetectionParamObject.detectionStatuses()) || searchDetectionParamObject.detectionStatuses().contains(DetectionStatus.IGNORED)) {
            detectionResponseDtos.addAll(baseDetectionService.getBaseDetectionEntitiesWithParameters(searchDetectionParamObject.deviceIds(), searchDetectionParamObject.startingDetectionDate(), searchDetectionParamObject.endDetectionDate())
                    .stream().map(DetectionResponseDto::new).toList());

        }

        Specification<DetectionEntity> entitySpecification = getDetectionQuery(searchDetectionParamObject);
        detectionResponseDtos.addAll(detectionRepository.findAll(entitySpecification)
                .stream().map(DetectionResponseDto::new).toList()
        );
        return detectionResponseDtos.stream().sorted(Comparator.comparing(DetectionResponseDto::getTimeRetrieved, Comparator.reverseOrder())).toList();

    }
}
