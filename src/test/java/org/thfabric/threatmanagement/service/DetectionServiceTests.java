package org.thfabric.threatmanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import org.thfabric.threatmanagement.entity.BaseDetectionEntity;
import org.thfabric.threatmanagement.entity.DetectionEntity;
import org.thfabric.threatmanagement.entity.DeviceEntity;
import org.thfabric.threatmanagement.entity.dto.request.ResolveDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.request.SavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.response.DetectionResponseDto;
import org.thfabric.threatmanagement.entity.enums.DetectionStatus;
import org.thfabric.threatmanagement.entity.enums.DeviceType;
import org.thfabric.threatmanagement.exception.WrongParameterException;
import org.thfabric.threatmanagement.repository.DetectionRepository;
import org.thfabric.threatmanagement.repository.DeviceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class DetectionServiceTests {

    private DetectionService underTest;
    @Mock
    private DetectionRepository detectionRepository;
    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private BaseDetectionService baseDetectionService;


    @BeforeEach
    void setUp() {
        underTest = new DetectionService(detectionRepository, deviceRepository, baseDetectionService);
    }

    @Test
    void saveDetection_WithWrongDeviceID() {
        SavingDetectionRequestDto savingDetectionRequestDto = new SavingDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now(), "asd", "asdf", UUID.randomUUID());

        given(deviceRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(WrongParameterException.class, () -> underTest.saveNewDetection(savingDetectionRequestDto));
        verify(detectionRepository, times(0)).save(Mockito.any(DetectionEntity.class));
    }

    @Test
    void saveDetection_WithExistingDetectionId() {
        SavingDetectionRequestDto savingDetectionRequestDto = new SavingDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now(), "asd", "asdf", UUID.randomUUID());
        DeviceEntity deviceEntity = new DeviceEntity();

        DetectionEntity detectionEntity = new DetectionEntity();
        given(deviceRepository.findById(any())).willReturn(Optional.of(deviceEntity));

        given(detectionRepository.findById(any())).willReturn(Optional.of(detectionEntity));
        assertThrows(WrongParameterException.class, () -> underTest.saveNewDetection(savingDetectionRequestDto));
    }

    @Test
    void saveDetection_WithCorrectData() {
        SavingDetectionRequestDto savingDetectionRequestDto = new SavingDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now(), "asd", "asdf", UUID.randomUUID());

        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setDeviceType(DeviceType.ANDROID);
        deviceEntity.setId(UUID.randomUUID());
        given(deviceRepository.findById(any())).willReturn(Optional.of(deviceEntity));
        given(detectionRepository.findById(any())).willReturn(Optional.empty());
        underTest.saveNewDetection(savingDetectionRequestDto);
        verify(detectionRepository, times(1)).save(Mockito.any(DetectionEntity.class));
    }

    @Test
    void resolveDetection_WithResolvedStatus() {
        ResolveDetectionRequestDto detectionRequestDto = new ResolveDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now());
        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setStatus(DetectionStatus.RESOLVED);

        given(detectionRepository.findById(any())).willReturn(Optional.of(detectionEntity));
        assertThrows(WrongParameterException.class, () -> underTest.resolveDetection(detectionRequestDto));
    }

    @Test
    void resolveDetection_WithNewStatus() {
        ResolveDetectionRequestDto detectionRequestDto = new ResolveDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now());
        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setStatus(DetectionStatus.NEW);

        given(detectionRepository.findById(any())).willReturn(Optional.of(detectionEntity));
        underTest.resolveDetection(detectionRequestDto);
        verify(detectionRepository, times(1)).save(detectionEntity);
    }

    @Test
    void getDetectionList_WithoutAnyFilters() {
        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setStatus(DetectionStatus.NEW);
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        detectionEntity.setDeviceEntity(deviceEntity);
        given(detectionRepository.findAll(any(Specification.class))).willReturn(new ArrayList<>(List.of(detectionEntity)));

        List<DetectionResponseDto> detectionEntities = underTest.getDetectionsByParameters(new SearchDetectionParamObject(null, null, null, null, null, null));
        assertEquals(1, detectionEntities.size());
        assertEquals(detectionEntity.getStatus(), detectionEntities.get(0).getStatus());
        assertEquals(deviceEntity.getId(), detectionEntities.get(0).getDeviceId());
    }

    @Test
    void getDetectionList_WithBaseDetections() {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setStatus(DetectionStatus.NEW);
        detectionEntity.setDeviceEntity(deviceEntity);
        detectionEntity.setTimeReported(LocalDateTime.now());
        detectionEntity.setTimeInserted(LocalDateTime.now());
        BaseDetectionEntity baseDetectionEntity = new BaseDetectionEntity();
        baseDetectionEntity.setTimeInserted(LocalDateTime.now().plusSeconds(2));
        baseDetectionEntity.setTimeReported(LocalDateTime.now().minusDays(2));
        baseDetectionEntity.setDeviceEntity(deviceEntity);
        given(detectionRepository.findAll(any(Specification.class))).willReturn(new ArrayList<>(List.of(detectionEntity)));
        given(baseDetectionService.getBaseDetectionEntitiesWithParameters(any(), any(), any())).willReturn(new ArrayList<>(List.of(baseDetectionEntity)));

        List<DetectionResponseDto> detectionEntities = underTest.getDetectionsByParameters(new SearchDetectionParamObject(null, null, null, null, null, null));
        assertEquals(2, detectionEntities.size());
        assertEquals(detectionEntity.getStatus(), detectionEntities.get(0).getStatus());
        assertEquals(deviceEntity.getId(), detectionEntities.get(0).getDeviceId());

        assertEquals(baseDetectionEntity.getDeviceEntity().getId(), detectionEntities.get(1).getDeviceId());
        assertFalse(detectionEntities.get(1).getTimeRetrieved().isAfter(detectionEntities.get(0).getTimeRetrieved()));
    }

}
