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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.thfabric.threatmanagement.controller.PeriodicalDetectionController;
import org.thfabric.threatmanagement.entity.DeviceEntity;
import org.thfabric.threatmanagement.entity.dto.request.PeriodicalSavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.enums.DeviceType;
import org.thfabric.threatmanagement.repository.BaseDetectionRepository;
import org.thfabric.threatmanagement.repository.DeviceRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class BaseDetectionControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    BaseDetectionRepository baseDetectionRepository;

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
    void performSaveEvent_WithNoDevice() throws Exception {
        baseDetectionRepository.deleteAll();

        PeriodicalSavingDetectionRequestDto eventRequestDTO = new PeriodicalSavingDetectionRequestDto(LocalDateTime.now(), UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.post(PeriodicalDetectionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(eventRequestDTO))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());


        assertEquals(0, baseDetectionRepository.findAll().size());
    }

    @Test
    void performSaveEvent() throws Exception {
        baseDetectionRepository.deleteAll();

        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setId(UUID.randomUUID());
        deviceEntity.setDeviceModel("asdf");
        deviceEntity.setDeviceType(DeviceType.ANDROID);
        deviceEntity.setOsVersion("1234");
        deviceRepository.save(deviceEntity);
        PeriodicalSavingDetectionRequestDto eventRequestDTO = new PeriodicalSavingDetectionRequestDto(LocalDateTime.now(), deviceEntity.getId());
        mockMvc.perform(MockMvcRequestBuilders.post(PeriodicalDetectionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(eventRequestDTO))
        ).andExpect(MockMvcResultMatchers.status().isOk());


        assertEquals(1, baseDetectionRepository.findAll().size());
        assertEquals(deviceEntity.getId(), baseDetectionRepository.findAll().get(0).getDeviceEntity().getId());
    }


}
