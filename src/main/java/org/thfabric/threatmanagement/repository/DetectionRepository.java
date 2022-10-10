package org.thfabric.threatmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.thfabric.threatmanagement.entity.DetectionEntity;

import java.util.UUID;

@Repository
public interface DetectionRepository extends JpaRepository<DetectionEntity, UUID>, JpaSpecificationExecutor<DetectionEntity> {

}
