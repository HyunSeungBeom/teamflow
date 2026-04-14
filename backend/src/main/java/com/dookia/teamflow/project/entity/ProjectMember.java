package com.dookia.teamflow.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PROJECT_MEMBER 엔티티. ERD v0.1 §3.
 */
@Entity
@Table(
    name = "project_member",
    uniqueConstraints = @UniqueConstraint(name = "uk_pm", columnNames = {"project_no", "user_no"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "project_no", nullable = false)
    private Long projectNo;

    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProjectMemberRole role;

    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @PrePersist
    void onCreate() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now();
        }
    }

    public void changeRole(ProjectMemberRole role) {
        this.role = role;
    }

    public static ProjectMember of(Long projectNo, Long userNo, ProjectMemberRole role) {
        return ProjectMember.builder()
            .projectNo(projectNo)
            .userNo(userNo)
            .role(role)
            .build();
    }
}
