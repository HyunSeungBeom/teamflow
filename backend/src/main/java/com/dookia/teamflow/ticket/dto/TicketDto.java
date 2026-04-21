package com.dookia.teamflow.ticket.dto;

import com.dookia.teamflow.ticket.entity.Ticket;
import com.dookia.teamflow.ticket.entity.TicketPriority;
import com.dookia.teamflow.ticket.entity.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 티켓 도메인 요청/응답 DTO. backend-conventions.md 규칙에 따라 {Domain}Dto.java 하나에 inner record 로 선언.
 */
public class TicketDto {

    private TicketDto() {
    }

    public record CreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 2, max = 200, message = "제목은 2~200자여야 합니다.")
        String title,

        String description,
        TicketStatus status,
        TicketPriority priority,
        Long assigneeUserNo,
        LocalDate dueDate
    ) {}

    public record UpdateRequest(
        @Size(min = 2, max = 200, message = "제목은 2~200자여야 합니다.")
        String title,

        String description,
        TicketStatus status,
        TicketPriority priority,
        Long assigneeUserNo,
        LocalDate dueDate
    ) {}

    public record Response(
        Long no,
        Long projectNo,
        String ticketKey,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        Long assigneeUserNo,
        int position,
        LocalDate dueDate
    ) {
        public static Response from(Ticket ticket) {
            return new Response(
                ticket.getNo(),
                ticket.getProjectNo(),
                ticket.getIssueKey(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getAssigneeNo(),
                ticket.getPosition(),
                ticket.getDueDate()
            );
        }
    }

    public record StatusChangeRequest(
        @NotNull(message = "status 는 필수입니다.") TicketStatus status
    ) {}

    public record PositionChangeRequest(
        @NotNull(message = "position 은 필수입니다.")
        @PositiveOrZero(message = "position 은 0 이상이어야 합니다.")
        Integer position
    ) {}

    public record StatusResponse(Long no, TicketStatus status) {
        public static StatusResponse from(Ticket ticket) {
            return new StatusResponse(ticket.getNo(), ticket.getStatus());
        }
    }

    public record PositionResponse(Long no, int position) {
        public static PositionResponse from(Ticket ticket) {
            return new PositionResponse(ticket.getNo(), ticket.getPosition());
        }
    }
}
