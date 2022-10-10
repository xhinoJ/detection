package org.thfabric.threatmanagement.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thfabric.threatmanagement.entity.BaseDetectionEntity;
import org.thfabric.threatmanagement.entity.DetectionEntity;
import org.thfabric.threatmanagement.entity.enums.DetectionStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DetectionQuerySpecificationService {

    /**
     * This methods contruct a Specification for {@link BaseDetectionEntity} based on the mentioned filters
     *
     * @param deviceIds         List of {@link BaseDetectionEntity}
     * @param startReportedDate Starting date
     * @param endReportedDate   Ending Date
     * @return @{@link Specification}
     */
    protected Specification<BaseDetectionEntity> getBaseDetectionQuery(List<UUID> deviceIds, LocalDateTime startReportedDate, LocalDateTime endReportedDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            handleSameFields(deviceIds, startReportedDate, endReportedDate, root, criteriaBuilder, predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * This methods contruct a Specification for {@link DetectionEntity} based on the mentioned filters
     *
     * @param searchDetectionParamObject List of {@link SearchDetectionParamObject}
     * @return @{@link Specification}
     */
    protected Specification<DetectionEntity> getDetectionQuery(SearchDetectionParamObject searchDetectionParamObject) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            handleSameFields(searchDetectionParamObject.deviceIds(), searchDetectionParamObject.startingDetectionDate(),
                    searchDetectionParamObject.endDetectionDate(), root, criteriaBuilder, predicates);

            if (!CollectionUtils.isEmpty(searchDetectionParamObject.detectionStatuses())) {
                Path<DetectionStatus> deviceField = root.get("status");
                predicates.add(deviceField.in(searchDetectionParamObject.detectionStatuses()));

            }

            if (!CollectionUtils.isEmpty(searchDetectionParamObject.applicationName())) {
                Path<String> appNamePath = root.get("appName");
                predicates.add(appNamePath.in(searchDetectionParamObject.applicationName()));
            }

            if (!CollectionUtils.isEmpty(searchDetectionParamObject.applicationType())) {
                Path<String> appNamePath = root.get("appType");
                predicates.add(appNamePath.in(searchDetectionParamObject.applicationType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void handleSameFields(List<UUID> deviceIds, LocalDateTime startReportedDate, LocalDateTime endReportedDate, Root<?> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (!CollectionUtils.isEmpty(deviceIds)) {
            Path<UUID> deviceField = root.get("deviceEntity").get("id");
            predicates.add(deviceField.in(deviceIds));
        }
        if (startReportedDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timeReported"), startReportedDate));
        }
        if (endReportedDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timeReported"), endReportedDate));
        }
    }


}
