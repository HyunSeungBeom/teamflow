package com.dookia.teamflow.ticket.controller;

import com.dookia.teamflow.auth.service.JwtService;
import com.dookia.teamflow.exception.EntityNotFoundException;
import com.dookia.teamflow.ticket.dto.TicketDto;
import com.dookia.teamflow.ticket.entity.TicketPriority;
import com.dookia.teamflow.ticket.entity.TicketStatus;
import com.dookia.teamflow.ticket.service.TicketService;
import com.dookia.teamflow.workspace.exception.WorkspaceAccessDeniedException;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TicketService ticketService;
    @MockBean JwtService jwtService;

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private void authenticatedAs(long userNo) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userNo, null, Collections.emptyList()));
    }

    private TicketDto.Response sampleResponse(Long no, String key) {
        return new TicketDto.Response(
            no, 50L, key, "로그인 화면 구현", "desc",
            TicketStatus.BACKLOG, TicketPriority.HIGH, null, 0, LocalDate.of(2026, 4, 25));
    }

    @Test
    @DisplayName("POST /api/projects/{projectNo}/tickets → 201 + Response")
    void create_returns201() throws Exception {
        authenticatedAs(2L);
        given(ticketService.create(eq(50L), eq(2L), any(TicketDto.CreateRequest.class)))
            .willReturn(sampleResponse(101L, "TF-1"));

        mockMvc.perform(post("/api/projects/50/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"로그인 화면 구현\",\"description\":\"desc\",\"priority\":\"HIGH\",\"dueDate\":\"2026-04-25\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.no", equalTo(101)))
            .andExpect(jsonPath("$.data.ticketKey", equalTo("TF-1")))
            .andExpect(jsonPath("$.data.status", equalTo("BACKLOG")));
    }

    @Test
    @DisplayName("POST create → 제목 1자는 400 (Size 검증)")
    void create_invalidTitle_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(post("/api/projects/50/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"A\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST create → 제목 누락은 400 (NotBlank)")
    void create_blankTitle_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(post("/api/projects/50/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST create → 프로젝트 없음(404)")
    void create_projectNotFound_returns404() throws Exception {
        authenticatedAs(2L);
        given(ticketService.create(eq(99L), eq(2L), any(TicketDto.CreateRequest.class)))
            .willThrow(new EntityNotFoundException("Project", 99L));

        mockMvc.perform(post("/api/projects/99/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"제목있음\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST create → 비멤버(403)")
    void create_forbidden_returns403() throws Exception {
        authenticatedAs(99L);
        given(ticketService.create(eq(50L), eq(99L), any(TicketDto.CreateRequest.class)))
            .willThrow(new WorkspaceAccessDeniedException("워크스페이스 멤버가 아닙니다."));

        mockMvc.perform(post("/api/projects/50/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"제목있음\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/projects/{projectNo}/tickets → 200 + 활성 티켓 목록")
    void list_returns200() throws Exception {
        authenticatedAs(2L);
        given(ticketService.listByProject(50L, 2L))
            .willReturn(List.of(sampleResponse(101L, "TF-1"), sampleResponse(102L, "TF-2")));

        mockMvc.perform(get("/api/projects/50/tickets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].ticketKey", equalTo("TF-1")))
            .andExpect(jsonPath("$.data[1].ticketKey", equalTo("TF-2")));
    }

    @Test
    @DisplayName("GET /api/tickets/{ticketNo} → 200 + 상세")
    void get_returns200() throws Exception {
        authenticatedAs(2L);
        given(ticketService.get(101L, 2L)).willReturn(sampleResponse(101L, "TF-1"));

        mockMvc.perform(get("/api/tickets/101"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.no", equalTo(101)))
            .andExpect(jsonPath("$.data.priority", equalTo("HIGH")));
    }

    @Test
    @DisplayName("GET /api/tickets/{ticketNo} → 없음 404")
    void get_notFound_returns404() throws Exception {
        authenticatedAs(2L);
        given(ticketService.get(99L, 2L)).willThrow(new EntityNotFoundException("Ticket", 99L));

        mockMvc.perform(get("/api/tickets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/tickets/{ticketNo} → 200 + 부분 수정 Response")
    void update_returns200() throws Exception {
        authenticatedAs(2L);
        TicketDto.Response updated = new TicketDto.Response(
            101L, 50L, "TF-1", "로그인 화면 구현", "desc",
            TicketStatus.IN_PROGRESS, TicketPriority.CRITICAL, 7L, 0, null);
        given(ticketService.update(eq(101L), eq(2L), any(TicketDto.UpdateRequest.class)))
            .willReturn(updated);

        mockMvc.perform(patch("/api/tickets/101")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_PROGRESS\",\"priority\":\"CRITICAL\",\"assigneeUserNo\":7}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status", equalTo("IN_PROGRESS")))
            .andExpect(jsonPath("$.data.assigneeUserNo", equalTo(7)));
    }

    @Test
    @DisplayName("DELETE /api/tickets/{ticketNo} → 204")
    void delete_returns204() throws Exception {
        authenticatedAs(2L);

        mockMvc.perform(delete("/api/tickets/101"))
            .andExpect(status().isNoContent());

        verify(ticketService).delete(101L, 2L);
    }

    @Test
    @DisplayName("DELETE /api/tickets/{ticketNo} → 없음 404")
    void delete_notFound_returns404() throws Exception {
        authenticatedAs(2L);
        willThrow(new EntityNotFoundException("Ticket", 99L))
            .given(ticketService).delete(99L, 2L);

        mockMvc.perform(delete("/api/tickets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/tickets/{ticketNo}/status → 200 + StatusResponse")
    void changeStatus_returns200() throws Exception {
        authenticatedAs(2L);
        given(ticketService.changeStatus(101L, 2L, TicketStatus.IN_PROGRESS))
            .willReturn(new TicketDto.StatusResponse(101L, TicketStatus.IN_PROGRESS));

        mockMvc.perform(patch("/api/tickets/101/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_PROGRESS\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.no", equalTo(101)))
            .andExpect(jsonPath("$.data.status", equalTo("IN_PROGRESS")));
    }

    @Test
    @DisplayName("PATCH /status → status 누락 400")
    void changeStatus_missingStatus_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(patch("/api/tickets/101/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /status → 잘못된 enum 400")
    void changeStatus_invalidEnum_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(patch("/api/tickets/101/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"NOT_A_STATUS\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /status → 없는 티켓 404")
    void changeStatus_notFound_returns404() throws Exception {
        authenticatedAs(2L);
        given(ticketService.changeStatus(99L, 2L, TicketStatus.DONE))
            .willThrow(new EntityNotFoundException("Ticket", 99L));

        mockMvc.perform(patch("/api/tickets/99/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"DONE\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/tickets/{ticketNo}/position → 200 + PositionResponse")
    void changePosition_returns200() throws Exception {
        authenticatedAs(2L);
        given(ticketService.changePosition(101L, 2L, 5))
            .willReturn(new TicketDto.PositionResponse(101L, 5));

        mockMvc.perform(patch("/api/tickets/101/position")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\":5}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.no", equalTo(101)))
            .andExpect(jsonPath("$.data.position", equalTo(5)));
    }

    @Test
    @DisplayName("PATCH /position → 음수 400")
    void changePosition_negative_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(patch("/api/tickets/101/position")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\":-1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /position → 누락 400")
    void changePosition_missing_returns400() throws Exception {
        authenticatedAs(2L);
        mockMvc.perform(patch("/api/tickets/101/position")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
