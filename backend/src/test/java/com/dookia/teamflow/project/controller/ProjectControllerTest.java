package com.dookia.teamflow.project.controller;

import com.dookia.teamflow.auth.service.JwtService;
import com.dookia.teamflow.project.dto.ProjectDto;
import com.dookia.teamflow.project.entity.ProjectStatus;
import com.dookia.teamflow.project.entity.ProjectVisibility;
import com.dookia.teamflow.project.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ProjectService projectService;
    @MockBean JwtService jwtService;

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private void authenticatedAs(long userNo) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userNo, null, Collections.emptyList()));
    }

    @Test
    @DisplayName("POST /api/workspaces/{wsNo}/projects → 201 + Response")
    void create_returns201() throws Exception {
        authenticatedAs(2L);
        given(projectService.create(eq(1L), eq(2L), any(ProjectDto.CreateRequest.class)))
            .willReturn(new ProjectDto.Response(
                50L, 1L, "TeamFlow", "TF", "desc", null, null,
                ProjectVisibility.PRIVATE, ProjectStatus.ACTIVE));

        mockMvc.perform(post("/api/workspaces/1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TeamFlow\",\"key\":\"TF\",\"description\":\"desc\",\"visibility\":\"PRIVATE\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.no", equalTo(50)))
            .andExpect(jsonPath("$.data.key", equalTo("TF")))
            .andExpect(jsonPath("$.data.status", equalTo("ACTIVE")));
    }

    @Test
    @DisplayName("POST create → 소문자 key 는 400")
    void create_invalidKey_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(post("/api/workspaces/1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"P\",\"key\":\"tf\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/workspaces/{wsNo}/projects → 200 + 요약 목록")
    void list_returns200() throws Exception {
        authenticatedAs(2L);
        given(projectService.listInWorkspace(1L, 2L))
            .willReturn(List.of(new ProjectDto.SummaryResponse(50L, 1L, "TF", "TF", "desc", 3L)));

        mockMvc.perform(get("/api/workspaces/1/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].no", equalTo(50)))
            .andExpect(jsonPath("$.data[0].memberCount", equalTo(3)));
    }

    @Test
    @DisplayName("GET /api/projects/{no} → 200 + 상세")
    void getDetail_returns200() throws Exception {
        authenticatedAs(2L);
        given(projectService.getDetail(50L, 2L))
            .willReturn(new ProjectDto.Response(
                50L, 1L, "TF", "TF", "desc", null, null,
                ProjectVisibility.PRIVATE, ProjectStatus.ACTIVE));

        mockMvc.perform(get("/api/projects/50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.no", equalTo(50)))
            .andExpect(jsonPath("$.data.visibility", equalTo("PRIVATE")));
    }
}
