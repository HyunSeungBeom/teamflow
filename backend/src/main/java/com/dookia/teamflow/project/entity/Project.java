package com.dookia.teamflow.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PROJECT 엔티티. ERD v0.1 §3 을 따른다.
 * ticket_counter 는 프로젝트별 티켓 시퀀스. 티켓 생성 시 원자적 +1 후 TICKET.ticket_key 조립에 사용.
 */
@Entity
@Table(
    name = "project",
    uniqueConstraints = @UniqueConstraint(name = "uk_project_key", columnNames = {"workspace_no", "`key`"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @Column(name = "workspace_no", nullable = false)
    private Long workspaceNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "`key`", nullable = false, length = 10)
    private String key;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String icon;

    @Column(length = 20)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProjectVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ProjectStatus status;

    @Column(name = "ticket_counter", nullable = false)
    private int ticketCounter;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createDate == null) {
            createDate = now;
        }
        if (updateDate == null) {
            updateDate = now;
        }
        if (visibility == null) {
            visibility = ProjectVisibility.PRIVATE;
        }
        if (status == null) {
            status = ProjectStatus.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void complete() {
        this.status = ProjectStatus.COMPLETED;
    }

    public int nextTicketNumber() {
        this.ticketCounter += 1;
        return this.ticketCounter;
    }

    public static Project create(Long workspaceNo, String name, String key, String description, ProjectVisibility visibility) {
        return Project.builder()
            .workspaceNo(workspaceNo)
            .name(name)
            .key(key)
            .description(description)
            .visibility(visibility != null ? visibility : ProjectVisibility.PRIVATE)
            .status(ProjectStatus.ACTIVE)
            .ticketCounter(0)
            .build();
    }
}
