package com.dookia.teamflow.ticket.service;

import com.dookia.teamflow.exception.EntityNotFoundException;
import com.dookia.teamflow.ticket.dto.TicketDto;
import com.dookia.teamflow.ticket.entity.Ticket;
import com.dookia.teamflow.ticket.entity.TicketPriority;
import com.dookia.teamflow.ticket.entity.TicketStatus;
import com.dookia.teamflow.ticket.repository.TicketRepository;
import com.dookia.teamflow.project.entity.Project;
import com.dookia.teamflow.project.repository.ProjectRepository;
import com.dookia.teamflow.workspace.exception.WorkspaceAccessDeniedException;
import com.dookia.teamflow.workspace.repository.WorkspaceMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock TicketRepository ticketRepository;
    @Mock ProjectRepository projectRepository;
    @Mock WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks TicketService ticketService;

    @Test
    @DisplayName("create → ticket_counter 증가 + ticketKey 조립(TF-1) + Ticket 저장")
    void create_success_assemblesIssueKey() {
        Project project = injectProjectNo(
            Project.create(10L, "TeamFlow", "TF", null, null), 50L);
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);
        given(ticketRepository.save(any(Ticket.class)))
            .willAnswer(inv -> injectIssueNo(inv.getArgument(0, Ticket.class), 100L));

        TicketDto.Response res = ticketService.create(50L, 2L, new TicketDto.CreateRequest(
            "로그인 화면 구현", "desc", null, TicketPriority.HIGH, null, LocalDate.of(2026, 4, 25)));

        assertThat(res.no()).isEqualTo(100L);
        assertThat(res.ticketKey()).isEqualTo("TF-1");
        assertThat(res.status()).isEqualTo(TicketStatus.BACKLOG);
        assertThat(res.priority()).isEqualTo(TicketPriority.HIGH);
        assertThat(project.getTicketCounter()).isEqualTo(1);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        assertThat(captor.getValue().getIssueKey()).isEqualTo("TF-1");
        assertThat(captor.getValue().getProjectNo()).isEqualTo(50L);
    }

    @Test
    @DisplayName("create → 연속 생성 시 TF-1, TF-2 순차 발급")
    void create_sequentialIssueKey() {
        Project project = injectProjectNo(
            Project.create(10L, "TeamFlow", "TF", null, null), 50L);
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);
        given(ticketRepository.save(any(Ticket.class)))
            .willAnswer(inv -> inv.getArgument(0, Ticket.class));

        TicketDto.Response first = ticketService.create(50L, 2L,
            new TicketDto.CreateRequest("A", null, null, null, null, null));
        TicketDto.Response second = ticketService.create(50L, 2L,
            new TicketDto.CreateRequest("B", null, null, null, null, null));

        assertThat(first.ticketKey()).isEqualTo("TF-1");
        assertThat(second.ticketKey()).isEqualTo("TF-2");
    }

    @Test
    @DisplayName("create → 프로젝트 없으면 EntityNotFoundException")
    void create_missingProject_throws() {
        given(projectRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.create(99L, 2L,
            new TicketDto.CreateRequest("A", null, null, null, null, null)))
            .isInstanceOf(EntityNotFoundException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("create → 워크스페이스 비멤버는 WorkspaceAccessDeniedException")
    void create_nonMember_denied() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 99L)).willReturn(false);

        assertThatThrownBy(() -> ticketService.create(50L, 99L,
            new TicketDto.CreateRequest("A", null, null, null, null, null)))
            .isInstanceOf(WorkspaceAccessDeniedException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("listByProject → position 오름차순으로 활성 티켓만 반환")
    void listByProject_returnsActiveOnly() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        Ticket a = injectIssueNo(Ticket.create(50L, "TF-1", "A", null, null, null, null, null, 0), 101L);
        Ticket b = injectIssueNo(Ticket.create(50L, "TF-2", "B", null, null, null, null, null, 1), 102L);
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);
        given(ticketRepository.findAllByProjectNoAndDeleteDateIsNullOrderByPositionAsc(50L))
            .willReturn(List.of(a, b));

        List<TicketDto.Response> list = ticketService.listByProject(50L, 2L);

        assertThat(list).extracting(TicketDto.Response::ticketKey).containsExactly("TF-1", "TF-2");
    }

    @Test
    @DisplayName("get → soft delete 된 티켓는 EntityNotFoundException")
    void get_softDeleted_throws() {
        given(ticketRepository.findByNoAndDeleteDateIsNull(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.get(99L, 2L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("update → 부분 수정: null 필드는 그대로, 지정된 필드만 반영")
    void update_partialFields() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        Ticket ticket = injectIssueNo(Ticket.create(
            50L, "TF-1", "원래제목", "원래설명",
            TicketStatus.BACKLOG, TicketPriority.MEDIUM, null, null, 0), 101L);
        given(ticketRepository.findByNoAndDeleteDateIsNull(101L)).willReturn(Optional.of(ticket));
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);

        TicketDto.Response res = ticketService.update(101L, 2L, new TicketDto.UpdateRequest(
            null, null, TicketStatus.IN_PROGRESS, TicketPriority.HIGH, 7L, null));

        assertThat(res.status()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(res.priority()).isEqualTo(TicketPriority.HIGH);
        assertThat(res.assigneeUserNo()).isEqualTo(7L);
        assertThat(res.title()).isEqualTo("원래제목");
    }

    @Test
    @DisplayName("delete → softDelete 호출 + deleteDate 세팅")
    void delete_setsSoftDeleteMarker() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        Ticket ticket = injectIssueNo(Ticket.create(
            50L, "TF-1", "A", null, null, null, null, null, 0), 101L);
        given(ticketRepository.findByNoAndDeleteDateIsNull(101L)).willReturn(Optional.of(ticket));
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);

        ticketService.delete(101L, 2L);

        assertThat(ticket.isDeleted()).isTrue();
        assertThat(ticket.getDeleteDate()).isNotNull();
    }

    @Test
    @DisplayName("delete → 티켓 없으면 EntityNotFoundException")
    void delete_missing_throws() {
        given(ticketRepository.findByNoAndDeleteDateIsNull(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.delete(99L, 2L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("changeStatus → BACKLOG → IN_PROGRESS 로 변경되고 StatusResponse 반환")
    void changeStatus_success() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        Ticket ticket = injectIssueNo(Ticket.create(
            50L, "TF-1", "A", null, TicketStatus.BACKLOG, null, null, null, 0), 101L);
        given(ticketRepository.findByNoAndDeleteDateIsNull(101L)).willReturn(Optional.of(ticket));
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);

        TicketDto.StatusResponse res = ticketService.changeStatus(101L, 2L, TicketStatus.IN_PROGRESS);

        assertThat(res.no()).isEqualTo(101L);
        assertThat(res.status()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("changeStatus → 없는 티켓 404")
    void changeStatus_missing_throws() {
        given(ticketRepository.findByNoAndDeleteDateIsNull(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.changeStatus(99L, 2L, TicketStatus.DONE))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("changeStatus → 비멤버 403")
    void changeStatus_nonMember_denied() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        Ticket ticket = injectIssueNo(Ticket.create(
            50L, "TF-1", "A", null, null, null, null, null, 0), 101L);
        given(ticketRepository.findByNoAndDeleteDateIsNull(101L)).willReturn(Optional.of(ticket));
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 99L)).willReturn(false);

        assertThatThrownBy(() -> ticketService.changeStatus(101L, 99L, TicketStatus.DONE))
            .isInstanceOf(WorkspaceAccessDeniedException.class);
    }

    @Test
    @DisplayName("changePosition → 0 → 5 로 이동되고 PositionResponse 반환")
    void changePosition_success() {
        Project project = injectProjectNo(Project.create(10L, "P", "TF", null, null), 50L);
        Ticket ticket = injectIssueNo(Ticket.create(
            50L, "TF-1", "A", null, null, null, null, null, 0), 101L);
        given(ticketRepository.findByNoAndDeleteDateIsNull(101L)).willReturn(Optional.of(ticket));
        given(projectRepository.findById(50L)).willReturn(Optional.of(project));
        given(workspaceMemberRepository.existsByWorkspaceNoAndUserNo(10L, 2L)).willReturn(true);

        TicketDto.PositionResponse res = ticketService.changePosition(101L, 2L, 5);

        assertThat(res.no()).isEqualTo(101L);
        assertThat(res.position()).isEqualTo(5);
        assertThat(ticket.getPosition()).isEqualTo(5);
    }

    @Test
    @DisplayName("changePosition → soft delete 된 티켓는 조회되지 않아 404")
    void changePosition_softDeleted_throws() {
        given(ticketRepository.findByNoAndDeleteDateIsNull(101L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.changePosition(101L, 2L, 3))
            .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------- helpers ----------

    private static Project injectProjectNo(Project p, long no) {
        try {
            Field f = Project.class.getDeclaredField("no");
            f.setAccessible(true);
            f.set(p, no);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return p;
    }

    private static Ticket injectIssueNo(Ticket i, long no) {
        try {
            Field f = Ticket.class.getDeclaredField("no");
            f.setAccessible(true);
            f.set(i, no);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return i;
    }
}
