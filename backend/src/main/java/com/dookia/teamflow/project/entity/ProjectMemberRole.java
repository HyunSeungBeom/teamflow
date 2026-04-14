package com.dookia.teamflow.project.entity;

/**
 * PROJECT_MEMBER.role 값. ERD v0.1 §3.
 * 소유자/관리자는 OWNER 로 통일하며, MEMBER 는 편집자, VIEWER 는 읽기 전용이다.
 */
public enum ProjectMemberRole {
    OWNER,
    MEMBER,
    VIEWER
}
