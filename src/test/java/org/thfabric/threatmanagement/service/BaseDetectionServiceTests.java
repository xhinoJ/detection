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
import org.thfabric.threatmanagement.entity.DeviceEntity;
import org.thfabric.threatmanagement.entity.dto.request.PeriodicalSavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.enums.DeviceType;
import org.thfabric.threatmanagement.exception.WrongParameterException;
import org.thfabric.threatmanagement.repository.BaseDetectionRepository;
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
class BaseDetectionServiceTests {

    private BaseDetectionService underTest;
    @Mock
    private BaseDetectionRepository detectionRepository;
    @Mock
    private DeviceRepository deviceRepository;

    @BeforeEach
    void setUp() {
        underTest = new BaseDetectionService(detectionRepository, deviceRepository);
    }

    @Test
    void savePeriodicalDetection_WithWrongDeviceID() {
        PeriodicalSavingDetectionRequestDto periodicalSavingDetectionRequestDto = new PeriodicalSavingDetectionRequestDto(LocalDateTime.now(), UUID.randomUUID());

        given(deviceRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(WrongParameterException.class, () -> underTest.savePeriodicalDetection(periodicalSavingDetectionRequestDto));
        verify(detectionRepository, times(0)).save(Mockito.any(BaseDetectionEntity.class));
    }

    @Test
    void savePeriodicalDetection_WithCorrectData() {
        PeriodicalSavingDetectionRequestDto periodicalSavingDetectionRequestDto = new PeriodicalSavingDetectionRequestDto(LocalDateTime.now(), UUID.randomUUID());
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        deviceEntity.setDeviceType(DeviceType.ANDROID);

        given(deviceRepository.findById(any())).willReturn(Optional.of(deviceEntity));

        underTest.savePeriodicalDetection(periodicalSavingDetectionRequestDto);
        verify(detectionRepository, times(1)).save(Mockito.any(BaseDetectionEntity.class));
    }

    @Test
    void getDetectionList_WithBaseDetections() {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        BaseDetectionEntity baseDetectionEntity = new BaseDetectionEntity();
        baseDetectionEntity.setTimeInserted(LocalDateTime.now());
        baseDetectionEntity.setTimeReported(LocalDateTime.now().minusDays(2));
        baseDetectionEntity.setDeviceEntity(deviceEntity);
        given(detectionRepository.findAll(any(Specification.class))).willReturn(new ArrayList<>(List.of(baseDetectionEntity)));

        List<BaseDetectionEntity> detectionEntities = underTest.getBaseDetectionEntitiesWithParameters(null, null, null);
        assertEquals(1, detectionEntities.size());

        assertEquals(baseDetectionEntity.getDeviceEntity(), detectionEntities.get(0).getDeviceEntity());
        assertEquals(baseDetectionEntity, detectionEntities.get(0));
        assertNotSame(LocalDateTime.now(), detectionEntities.get(0).getTimeInserted());

    }

}
