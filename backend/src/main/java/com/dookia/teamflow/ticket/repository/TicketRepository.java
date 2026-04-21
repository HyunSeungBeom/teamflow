package com.dookia.teamflow.ticket.repository;

import com.dookia.teamflow.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByProjectNoAndDeleteDateIsNullOrderByPositionAsc(Long projectNo);

    Optional<Ticket> findByNoAndDeleteDateIsNull(Long no);

    boolean existsByProjectNoAndTicketKey(Long projectNo, String ticketKey);
}
