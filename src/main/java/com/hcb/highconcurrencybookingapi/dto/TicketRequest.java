package com.hcb.highconcurrencybookingapi.dto;

import java.io.Serializable;
import java.util.UUID;

public record TicketRequest(
        UUID requestId,
        Long seatId,
        String userId
) implements Serializable {}