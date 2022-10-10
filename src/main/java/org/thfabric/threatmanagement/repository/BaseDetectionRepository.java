package org.thfabric.threatmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.thfabric.threatmanagement.entity.BaseDetectionEntity;

import java.util.UUID;

@Repository
public interface BaseDetectionRepository extends JpaRepository<BaseDetectionEntity, UUID>, JpaSpecificationExecutor<BaseDetectionEntity> {
}
