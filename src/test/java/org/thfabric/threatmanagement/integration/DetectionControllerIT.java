package org.thfabric.threatmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.thfabric.threatmanagement.controller.DetectionController;
import org.thfabric.threatmanagement.entity.BaseDetectionEntity;
import org.thfabric.threatmanagement.entity.DetectionEntity;
import org.thfabric.threatmanagement.entity.DeviceEntity;
import org.thfabric.threatmanagement.entity.dto.request.ResolveDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.request.SavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.response.DetectionResponseDto;
import org.thfabric.threatmanagement.entity.enums.DeviceType;
import org.thfabric.threatmanagement.repository.BaseDetectionRepository;
import org.thfabric.threatmanagement.repository.DetectionRepository;
import org.thfabric.threatmanagement.repository.DeviceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thfabric.threatmanagement.entity.enums.DetectionStatus.NEW;
import static org.thfabric.threatmanagement.entity.enums.DetectionStatus.RESOLVED;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class DetectionControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    BaseDetectionRepository baseDetectionRepository;
    @Autowired
    DetectionRepository detectionRepository;

    @Autowired
    DeviceRepository deviceRepository;

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadDetectionsWithFilters() throws Exception {
        baseDetectionRepository.deleteAll();
        detectionRepository.deleteAll();
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        deviceEntity.setDeviceModel("asdf");
        deviceEntity.setDeviceType(DeviceType.ANDROID);
        deviceEntity.setOsVersion("1234");
        deviceRepository.save(deviceEntity);

        BaseDetectionEntity baseDetectionEntity = new BaseDetectionEntity();
        baseDetectionEntity.setId(UUID.randomUUID());
        baseDetectionEntity.setDeviceEntity(deviceEntity);
        baseDetectionEntity.setTimeReported(LocalDateTime.now());
        baseDetectionEntity.setTimeInserted(LocalDateTime.now());
        baseDetectionRepository.save(baseDetectionEntity);

        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setId(UUID.randomUUID());
        detectionEntity.setDeviceEntity(deviceEntity);
        detectionEntity.setTimeInserted(LocalDateTime.now().minusYears(2));
        detectionEntity.setTimeReported(LocalDateTime.now().minusYears(2));
        detectionEntity.setAppType("asf");
        detectionEntity.setAppName("wer");
        detectionEntity.setStatus(RESOLVED);
        detectionRepository.save(detectionEntity);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("deviceId", deviceEntity.getId().toString());
        requestParams.add("status", Stream.of(NEW, RESOLVED, RESOLVED).map(Enum::toString).collect(joining(", ")));
        requestParams.add("detectedDateFrom", LocalDateTime.now().minusYears(1).toString());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(DetectionController.BASE_URL, requestParams)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<DetectionResponseDto> detectionResponseDtoList = new ObjectMapper().readValue((mvcResult.getResponse().getContentAsString()), List.class);

        assertEquals(2, detectionResponseDtoList.size());
    }

    @Test
    void resolveDetection_WithWrongDetectionId() throws Exception {
        baseDetectionRepository.deleteAll();
        detectionRepository.deleteAll();

        ResolveDetectionRequestDto detectionRequestDto = new ResolveDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now());
        mockMvc.perform(MockMvcRequestBuilders.put(DetectionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(detectionRequestDto))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void resolveDetection_WithWrongStatus() throws Exception {
        baseDetectionRepository.deleteAll();
        detectionRepository.deleteAll();
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        deviceEntity.setDeviceModel("asdf");
        deviceEntity.setDeviceType(DeviceType.ANDROID);
        deviceEntity.setOsVersion("1234");
        deviceRepository.save(deviceEntity);

        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setId(UUID.randomUUID());
        detectionEntity.setDeviceEntity(deviceEntity);
        detectionEntity.setTimeInserted(LocalDateTime.now().minusYears(2));
        detectionEntity.setTimeReported(LocalDateTime.now().minusYears(2));
        detectionEntity.setAppType("asf");
        detectionEntity.setAppName("wer");
        detectionEntity.setStatus(RESOLVED);
        detectionRepository.save(detectionEntity);

        ResolveDetectionRequestDto detectionRequestDto = new ResolveDetectionRequestDto(detectionEntity.getId(), LocalDateTime.now());
        mockMvc.perform(MockMvcRequestBuilders.put(DetectionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(detectionRequestDto))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void resolveDetection_WithCorrectData() throws Exception {
        baseDetectionRepository.deleteAll();
        detectionRepository.deleteAll();
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        deviceEntity.setDeviceModel("asdf");
        deviceEntity.setDeviceType(DeviceType.ANDROID);
        deviceEntity.setOsVersion("1234");
        deviceRepository.save(deviceEntity);

        DetectionEntity detectionEntity = new DetectionEntity();
        detectionEntity.setId(UUID.randomUUID());
        detectionEntity.setDeviceEntity(deviceEntity);
        detectionEntity.setTimeInserted(LocalDateTime.now().minusYears(2));
        detectionEntity.setTimeReported(LocalDateTime.now().minusYears(2));
        detectionEntity.setAppType("asf");
        detectionEntity.setAppName("wer");
        detectionEntity.setStatus(NEW);
        detectionRepository.save(detectionEntity);

        ResolveDetectionRequestDto detectionRequestDto = new ResolveDetectionRequestDto(detectionEntity.getId(), LocalDateTime.now());
        mockMvc.perform(MockMvcRequestBuilders.put(DetectionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(detectionRequestDto))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void saveDetection_WithCorrectData() throws Exception {
        baseDetectionRepository.deleteAll();
        detectionRepository.deleteAll();
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        deviceEntity.setDeviceModel("asdf");
        deviceEntity.setDeviceType(DeviceType.ANDROID);
        deviceEntity.setOsVersion("1234");
        deviceRepository.save(deviceEntity);


        SavingDetectionRequestDto detectionRequestDto = new SavingDetectionRequestDto(UUID.randomUUID(), LocalDateTime.now(), "tst", "social", deviceEntity.getId());
        mockMvc.perform(MockMvcRequestBuilders.post(DetectionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(detectionRequestDto))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(1, detectionRepository.findAll().size());

    }

}
