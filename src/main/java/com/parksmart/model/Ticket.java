package com.parksmart.model;

import java.time.LocalDateTime;

/** A parking ticket. It is "active" until the vehicle leaves. */
public class Ticket {

    private String ticketId;
    private Vehicle vehicle;
    private ParkingSlot slot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;


    public Ticket(String ticketId, Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be empty.");
        }

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }

        if (slot == null) {
            throw new IllegalArgumentException("Parking slot cannot be null.");
        }

        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time cannot be null.");
        }

        this.ticketId = ticketId.trim().toUpperCase();
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
        this.exitTime = null;
        this.fee = 0.0;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    /** Null while the vehicle is still parked. */
    public LocalDateTime getExitTime() {
        return exitTime;
    }

    /** 0 while the vehicle is still parked. */
    public double getFee() {
        return fee;
    }

    /** True until the ticket is closed. */
    public boolean isActive() {
        return exitTime == null;
    }

    /**
     * Closes the ticket when the vehicle leaves.
     * Rule: if the ticket is already closed -> throw IllegalStateException.
     */
    public void close(LocalDateTime exitTime, double fee) {
        if (!isActive()) {
            throw new IllegalStateException(
                    "This ticket has already been closed."
            );
        }

        if (exitTime == null || exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException(
                    "Invalid exit time."
            );
        }

        if (!Double.isFinite(fee) || fee < 0) {
            throw new IllegalArgumentException(
                    "Invalid parking fee."
            );
        }

        this.exitTime = exitTime;
        this.fee = fee;
    }
}
