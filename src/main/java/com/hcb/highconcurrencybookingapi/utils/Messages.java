package com.hcb.highconcurrencybookingapi.utils;

public final class Messages {

    private Messages() {}

    public static final class Seat {
        private Seat() {}

        public static final String NOT_FOUND = "Seat not found in the system.";
        public static final String ALREADY_RESERVED = "This seat has already been reserved by another user.";
        public static final String LOCK_FAILED = "We were unable to lock the seat. Please try again.";
    }

    public static final class Ticket {
        private Ticket() {}

        public static final String PROCESSED_SUCCESS = "Ticket processed successfully!";
        public static final String PROCESSING_ERROR = "Error processing ticket.";
    }
}