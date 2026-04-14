package com.dookia.teamflow.project.repository;

import com.dookia.teamflow.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    long countByProjectNo(Long projectNo);
}
