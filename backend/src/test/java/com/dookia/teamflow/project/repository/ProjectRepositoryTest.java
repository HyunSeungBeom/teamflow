package com.dookia.teamflow.project.repository;

import com.dookia.teamflow.project.entity.Project;
import com.dookia.teamflow.project.entity.ProjectMember;
import com.dookia.teamflow.project.entity.ProjectMemberRole;
import com.dookia.teamflow.project.entity.ProjectStatus;
import com.dookia.teamflow.project.entity.ProjectVisibility;
import com.dookia.teamflow.user.entity.User;
import com.dookia.teamflow.user.repository.UserRepository;
import com.dookia.teamflow.workspace.entity.Workspace;
import com.dookia.teamflow.workspace.repository.WorkspaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectRepositoryTest {

    @Autowired ProjectRepository projectRepository;
    @Autowired ProjectMemberRepository projectMemberRepository;
    @Autowired WorkspaceRepository workspaceRepository;
    @Autowired UserRepository userRepository;
    @Autowired TestEntityManager em;

    @Test
    @DisplayName("Project.create 는 visibility/status 기본값과 ticket_counter=0 으로 저장된다")
    void save_defaults() {
        Workspace ws = workspaceRepository.save(Workspace.create("W"));

        Project saved = projectRepository.save(
            Project.create(ws.getNo(), "TeamFlow", "TF", "desc", null));
        em.flush();
        em.clear();

        Project found = projectRepository.findById(saved.getNo()).orElseThrow();
        assertThat(found.getWorkspaceNo()).isEqualTo(ws.getNo());
        assertThat(found.getKey()).isEqualTo("TF");
        assertThat(found.getVisibility()).isEqualTo(ProjectVisibility.PRIVATE);
        assertThat(found.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(found.getTicketCounter()).isZero();
    }

    @Test
    @DisplayName("existsByWorkspaceNoAndKey — 같은 워크스페이스 내 key 중복 탐지")
    void existsByWorkspaceNoAndKey() {
        Workspace ws = workspaceRepository.save(Workspace.create("W"));
        projectRepository.save(Project.create(ws.getNo(), "A", "TF", null, ProjectVisibility.PRIVATE));
        em.flush();
        em.clear();

        assertThat(projectRepository.existsByWorkspaceNoAndKey(ws.getNo(), "TF")).isTrue();
        assertThat(projectRepository.existsByWorkspaceNoAndKey(ws.getNo(), "OTHER")).isFalse();
    }

    @Test
    @DisplayName("findAllByWorkspaceNoOrderByCreateDateDesc — 최신순 정렬")
    void listInWorkspace_orderedByCreateDateDesc() throws Exception {
        Workspace ws = workspaceRepository.save(Workspace.create("W"));
        Project first = projectRepository.save(Project.create(ws.getNo(), "First", "AA", null, null));
        Thread.sleep(5);
        Project second = projectRepository.save(Project.create(ws.getNo(), "Second", "BB", null, null));
        em.flush();
        em.clear();

        var list = projectRepository.findAllByWorkspaceNoOrderByCreateDateDesc(ws.getNo());
        assertThat(list).extracting(Project::getKey).containsExactly(second.getKey(), first.getKey());
    }

    @Test
    @DisplayName("ProjectMember — projectNo + userNo 고유 + countByProjectNo")
    void projectMember_count() {
        Workspace ws = workspaceRepository.save(Workspace.create("W"));
        Project project = projectRepository.save(Project.create(ws.getNo(), "P", "PK", null, null));
        User owner = userRepository.save(User.createFromGoogle("s", "o@x.com", "O", null));

        projectMemberRepository.save(ProjectMember.of(project.getNo(), owner.getNo(), ProjectMemberRole.OWNER));
        em.flush();
        em.clear();

        assertThat(projectMemberRepository.countByProjectNo(project.getNo())).isEqualTo(1);
    }
}
