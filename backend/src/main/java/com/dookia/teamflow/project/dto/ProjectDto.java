package com.dookia.teamflow.project.dto;

import com.dookia.teamflow.project.entity.Project;
import com.dookia.teamflow.project.entity.ProjectStatus;
import com.dookia.teamflow.project.entity.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 프로젝트 도메인 요청/응답 DTO. backend-conventions.md 규칙에 따라 {Domain}Dto.java 하나에 inner record 로 선언.
 */
public class ProjectDto {

    private ProjectDto() {
    }

    public record CreateRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 100, message = "이름은 2~100자여야 합니다.")
        String name,

        @NotBlank(message = "key 는 필수입니다.")
        @Pattern(regexp = "^[A-Z][A-Z0-9]{1,9}$", message = "key 는 2~10자 대문자/숫자(첫 글자는 대문자)여야 합니다.")
        String key,

        @Size(max = 500) String description,
        ProjectVisibility visibility
    ) {}

    public record SummaryResponse(
        Long no,
        Long workspaceNo,
        String name,
        String key,
        String description,
        long memberCount
    ) {
        public static SummaryResponse of(Project p, long memberCount) {
            return new SummaryResponse(
                p.getNo(), p.getWorkspaceNo(), p.getName(), p.getKey(), p.getDescription(), memberCount
            );
        }
    }

    public record Response(
        Long no,
        Long workspaceNo,
        String name,
        String key,
        String description,
        String icon,
        String color,
        ProjectVisibility visibility,
        ProjectStatus status
    ) {
        public static Response from(Project p) {
            return new Response(
                p.getNo(), p.getWorkspaceNo(), p.getName(), p.getKey(),
                p.getDescription(), p.getIcon(), p.getColor(),
                p.getVisibility(), p.getStatus()
            );
        }
    }
}
