package org.thfabric.threatmanagement.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thfabric.threatmanagement.entity.dto.request.PeriodicalSavingDetectionRequestDto;
import org.thfabric.threatmanagement.exception.WrongParameterException;
import org.thfabric.threatmanagement.service.BaseDetectionService;

@Slf4j
@RestController
@AllArgsConstructor
public class PeriodicalDetectionController {

    public static final String BASE_URL = "/app/detection/periodical";
    final BaseDetectionService baseDetectionService;

    @ApiOperation(value = "Rest method used to save periodical detections ( no threats )")
    @PostMapping(path = BASE_URL)
    public ResponseEntity<String> savePeriodicalDetection(@RequestBody @ApiParam(value = "Request body containing " +
            "detection date and device id") PeriodicalSavingDetectionRequestDto periodicalSavingDetectionRequestDto) throws WrongParameterException {
        try {
            baseDetectionService.savePeriodicalDetection(periodicalSavingDetectionRequestDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
