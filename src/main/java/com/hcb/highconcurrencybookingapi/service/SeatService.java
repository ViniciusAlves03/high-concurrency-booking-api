package com.hcb.highconcurrencybookingapi.service;

import com.hcb.highconcurrencybookingapi.dto.TicketRequest;
import com.hcb.highconcurrencybookingapi.exception.BusinessException;
import com.hcb.highconcurrencybookingapi.model.Seat;
import com.hcb.highconcurrencybookingapi.repository.SeatRepository;
import com.hcb.highconcurrencybookingapi.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional
    public void reservarAssento(TicketRequest request) {
        Seat seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new BusinessException(Messages.Seat.NOT_FOUND));

        if (seat.getReservedBy() != null) {
            throw new BusinessException(Messages.Seat.ALREADY_RESERVED);
        }

        seat.setReservedBy(request.userId());
        seatRepository.save(seat);
    }
}