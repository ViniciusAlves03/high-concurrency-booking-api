package com.hcb.highconcurrencybookingapi.repository;

import com.hcb.highconcurrencybookingapi.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
