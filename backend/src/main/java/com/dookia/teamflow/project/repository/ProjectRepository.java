package com.dookia.teamflow.project.repository;

import com.dookia.teamflow.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByWorkspaceNoOrderByCreateDateDesc(Long workspaceNo);

    boolean existsByWorkspaceNoAndKey(Long workspaceNo, String key);
}
