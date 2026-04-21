package com.dookia.teamflow.ticket.service;

import com.dookia.teamflow.exception.EntityNotFoundException;
import com.dookia.teamflow.ticket.dto.TicketDto;
import com.dookia.teamflow.ticket.entity.Ticket;
import com.dookia.teamflow.ticket.entity.TicketStatus;
import com.dookia.teamflow.ticket.repository.TicketRepository;
import com.dookia.teamflow.project.entity.Project;
import com.dookia.teamflow.project.repository.ProjectRepository;
import com.dookia.teamflow.workspace.exception.WorkspaceAccessDeniedException;
import com.dookia.teamflow.workspace.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 티켓 도메인 비즈니스 로직. Sprint 2 §2.2 + HANDOFF.md §2 를 구현한다.
 * 삭제는 delete_date 를 채우는 soft delete (RISK-IMPACT 2026-04-20 결정).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public TicketDto.Response create(Long projectNo, Long userNo, TicketDto.CreateRequest request) {
        Project project = projectRepository.findById(projectNo)
            .orElseThrow(() -> new EntityNotFoundException("Project", projectNo));
        requireWorkspaceMember(project.getWorkspaceNo(), userNo);

        int ticketNumber = project.nextTicketNumber();
        String ticketKey = project.getKey() + "-" + ticketNumber;

        Ticket saved = ticketRepository.save(Ticket.create(
            projectNo,
            ticketKey,
            request.title(),
            request.description(),
            request.status(),
            request.priority(),
            request.assigneeUserNo(),
            request.dueDate(),
            0
        ));
        return TicketDto.Response.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketDto.Response> listByProject(Long projectNo, Long userNo) {
        Project project = projectRepository.findById(projectNo)
            .orElseThrow(() -> new EntityNotFoundException("Project", projectNo));
        requireWorkspaceMember(project.getWorkspaceNo(), userNo);

        return ticketRepository.findAllByProjectNoAndDeleteDateIsNullOrderByPositionAsc(projectNo).stream()
            .map(TicketDto.Response::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public TicketDto.Response get(Long ticketNo, Long userNo) {
        Ticket ticket = loadActive(ticketNo);
        requireProjectMember(ticket.getProjectNo(), userNo);
        return TicketDto.Response.from(ticket);
    }

    public TicketDto.Response update(Long ticketNo, Long userNo, TicketDto.UpdateRequest request) {
        Ticket ticket = loadActive(ticketNo);
        requireProjectMember(ticket.getProjectNo(), userNo);

        if (request.title() != null || request.description() != null || request.dueDate() != null) {
            ticket.updateDetails(request.title(), request.description(), request.dueDate());
        }
        if (request.status() != null) {
            ticket.changeStatus(request.status());
        }
        if (request.priority() != null) {
            ticket.changePriority(request.priority());
        }
        if (request.assigneeUserNo() != null) {
            ticket.assignTo(request.assigneeUserNo());
        }
        return TicketDto.Response.from(ticket);
    }

    public void delete(Long ticketNo, Long userNo) {
        Ticket ticket = loadActive(ticketNo);
        requireProjectMember(ticket.getProjectNo(), userNo);
        ticket.softDelete();
    }

    /**
     * 칸반 보드 드래그 앤 드롭의 컬럼 간 이동. 상태만 변경하고 최소 응답을 돌려준다.
     */
    public TicketDto.StatusResponse changeStatus(Long ticketNo, Long userNo, TicketStatus status) {
        Ticket ticket = loadActive(ticketNo);
        requireProjectMember(ticket.getProjectNo(), userNo);
        ticket.changeStatus(status);
        return TicketDto.StatusResponse.from(ticket);
    }

    /**
     * 같은 컬럼 내 순서 변경. position 만 변경하고 최소 응답을 돌려준다.
     * 다중 티켓 rebalancing 은 본 API 범위 외 — 클라이언트가 드롭 타겟 position 을 계산해 전달한다.
     */
    public TicketDto.PositionResponse changePosition(Long ticketNo, Long userNo, int position) {
        Ticket ticket = loadActive(ticketNo);
        requireProjectMember(ticket.getProjectNo(), userNo);
        ticket.moveTo(position);
        return TicketDto.PositionResponse.from(ticket);
    }

    // --- helpers ------------------------------------------------------------

    private Ticket loadActive(Long ticketNo) {
        return ticketRepository.findByNoAndDeleteDateIsNull(ticketNo)
            .orElseThrow(() -> new EntityNotFoundException("Ticket", ticketNo));
    }

    private void requireProjectMember(Long projectNo, Long userNo) {
        Project project = projectRepository.findById(projectNo)
            .orElseThrow(() -> new EntityNotFoundException("Project", projectNo));
        requireWorkspaceMember(project.getWorkspaceNo(), userNo);
    }

    private void requireWorkspaceMember(Long workspaceNo, Long userNo) {
        if (!workspaceMemberRepository.existsByWorkspaceNoAndUserNo(workspaceNo, userNo)) {
            throw new WorkspaceAccessDeniedException("워크스페이스 멤버가 아닙니다.");
        }
    }
}
