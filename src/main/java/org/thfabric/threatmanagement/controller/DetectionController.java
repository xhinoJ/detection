package org.thfabric.threatmanagement.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thfabric.threatmanagement.entity.dto.request.ResolveDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.request.SavingDetectionRequestDto;
import org.thfabric.threatmanagement.entity.dto.response.DetectionResponseDto;
import org.thfabric.threatmanagement.entity.enums.DetectionStatus;
import org.thfabric.threatmanagement.service.DetectionService;
import org.thfabric.threatmanagement.service.SearchDetectionParamObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class DetectionController {

    public static final String BASE_URL = "/app/detection";
    final DetectionService detectionService;

    @ApiOperation(value = "Rest method designed to save new detection.")
    @PostMapping(path = BASE_URL)
    public ResponseEntity<String> saveNewDetection(@RequestBody SavingDetectionRequestDto savingDetectionRequestDto) {
        try {
            detectionService.saveNewDetection(savingDetectionRequestDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Rest method to resolve a detection." +
            "It throws an exception in case status detection is already resolved or detection does not exist")
    @PutMapping(path = BASE_URL)
    public ResponseEntity<String> resolveDetection(@RequestBody @ApiParam(value = "ResolveDetectionRequestDto parameter" +
            "which contains detection Id and resolved date. ")
                                                           ResolveDetectionRequestDto detectionRequestDto) {
        try {
            detectionService.resolveDetection(detectionRequestDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Rest method used to retrieve all the detections based on the mentioned criteria")
    @GetMapping(path = BASE_URL)
    public ResponseEntity<List<DetectionResponseDto>> getDetections(
            @RequestParam(name = "deviceIds", required = false)
            @ApiParam(value = "List of device Ids") List<UUID> deviceIds
            , @RequestParam(name = "detectionStatuses", required = false)
            @ApiParam(value = "List of detection statuses") List<DetectionStatus> detectionStatuses
            , @RequestParam(name = "detectedDateFrom", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @ApiParam(value = "Date the detection was recognized (starting from )") LocalDateTime startingDetectionDate
            , @RequestParam(name = "detectedDateTo", required = false) @ApiParam(value = "Date the detection was recognized ( until )")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                    LocalDateTime endDetectionDate
            , @RequestParam(name = "applicationName", required = false) @ApiParam(value = "List of application names")
                    List<String> applicationNames
            , @RequestParam(name = "applicationType", required = false) @ApiParam(value = "List of application types")
                    List<String> applicationTypes
    ) {
        List<DetectionResponseDto> detectionResponseDtos = detectionService.getDetectionsByParameters(new SearchDetectionParamObject(deviceIds, detectionStatuses, startingDetectionDate, endDetectionDate, applicationNames, applicationTypes));

        return ResponseEntity.ok(detectionResponseDtos);
    }

}
