package com.dookia.teamflow.project.repository;

import com.dookia.teamflow.project.entity.Project;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByWorkspaceNoOrderByCreateDateDesc(Long workspaceNo);

    boolean existsByWorkspaceNoAndKey(Long workspaceNo, String key);

    /**
     * 티켓 생성 시 ticket_counter 원자적 증가를 위한 행 잠금 조회.
     * 동시 티켓 생성으로 인한 ticketKey 중복을 방지한다 (SELECT ... FOR UPDATE).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Project p WHERE p.no = :projectNo")
    Optional<Project> findByIdForUpdate(@Param("projectNo") Long projectNo);
}
