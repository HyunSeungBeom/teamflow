package com.dookia.teamflow.ticket.controller;

import com.dookia.teamflow.dto.ApiResponse;
import com.dookia.teamflow.ticket.dto.TicketDto;
import com.dookia.teamflow.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 티켓 엔드포인트. Sprint 2 HANDOFF.md §2 를 따른다.
 *  - POST   /api/projects/{projectNo}/tickets
 *  - GET    /api/projects/{projectNo}/tickets
 *  - GET    /api/tickets/{ticketNo}
 *  - PATCH  /api/tickets/{ticketNo}
 *  - DELETE /api/tickets/{ticketNo}
 */
@Tag(name = "Ticket", description = "티켓 CRUD (칸반 보드)")
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "티켓 생성")
    @PostMapping("/api/projects/{projectNo}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TicketDto.Response> create(@AuthenticationPrincipal Long userNo, @PathVariable Long projectNo, @Valid @RequestBody TicketDto.CreateRequest request) {
        return ApiResponse.success(ticketService.create(projectNo, userNo, request));
    }

    @Operation(summary = "프로젝트 내 티켓 목록 (활성 티켓만, position 오름차순)")
    @GetMapping("/api/projects/{projectNo}/tickets")
    public ApiResponse<List<TicketDto.Response>> list(@AuthenticationPrincipal Long userNo, @PathVariable Long projectNo) {
        return ApiResponse.success(ticketService.listByProject(projectNo, userNo));
    }

    @Operation(summary = "티켓 상세")
    @GetMapping("/api/tickets/{ticketNo}")
    public ApiResponse<TicketDto.Response> getDetail(@AuthenticationPrincipal Long userNo, @PathVariable Long ticketNo) {
        return ApiResponse.success(ticketService.get(ticketNo, userNo));
    }

    @Operation(summary = "티켓 수정 (부분)")
    @PatchMapping("/api/tickets/{ticketNo}")
    public ApiResponse<TicketDto.Response> update(@AuthenticationPrincipal Long userNo, @PathVariable Long ticketNo, @Valid @RequestBody TicketDto.UpdateRequest request) {
        return ApiResponse.success(ticketService.update(ticketNo, userNo, request));
    }

    @Operation(summary = "티켓 삭제 (soft delete)")
    @DeleteMapping("/api/tickets/{ticketNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userNo, @PathVariable Long ticketNo) {
        ticketService.delete(ticketNo, userNo);
    }

    @Operation(summary = "티켓 상태 변경 (드래그 앤 드롭: 컬럼 간 이동)")
    @PatchMapping("/api/tickets/{ticketNo}/status")
    public ApiResponse<TicketDto.StatusResponse> changeStatus(@AuthenticationPrincipal Long userNo, @PathVariable Long ticketNo, @Valid @RequestBody TicketDto.StatusChangeRequest request) {
        return ApiResponse.success(ticketService.changeStatus(ticketNo, userNo, request.status()));
    }

    @Operation(summary = "티켓 순서 변경 (같은 컬럼 내)")
    @PatchMapping("/api/tickets/{ticketNo}/position")
    public ApiResponse<TicketDto.PositionResponse> changePosition(@AuthenticationPrincipal Long userNo, @PathVariable Long ticketNo, @Valid @RequestBody TicketDto.PositionChangeRequest request) {
        return ApiResponse.success(ticketService.changePosition(ticketNo, userNo, request.position()));
    }
}
