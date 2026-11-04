package com.parksmart.model;

import com.parksmart.exception.ParkingException;
import com.parksmart.exception.ParkingFullException;
import com.parksmart.exception.TicketNotFoundException;
import com.parksmart.exception.VehicleAlreadyParkedException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The whole car park: its slots, the tickets of parked vehicles,
 * the history of finished tickets and the total earnings.
 */
public class ParkingLot {
    private String name;
    private List<ParkingSlot> slots;
    private List<Ticket> activeTickets;
    private List<Ticket> completedTickets;
    private int ticketCounter;
    private double totalEarnings;

    /** Rule: name cannot be empty -> throw IllegalArgumentException. */
    public ParkingLot(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking lot name cannot be empty.");
        }

        this.name = name.trim();
        this.slots = new ArrayList<>();
        this.activeTickets = new ArrayList<>();
        this.completedTickets = new ArrayList<>();

        this.ticketCounter = 1001;
        this.totalEarnings = 0.0;
    }

    public String getName() {
        return name;
    }

    /**
     * Adds a new empty slot.
     * Rule: two slots cannot have the same ID (ignore capital/small letters)
     *       -> throw IllegalArgumentException.
     */
    public void addSlot(String slotId, SlotType type) {
        ParkingSlot newSlot = new ParkingSlot(slotId, type);
        for (ParkingSlot slot : slots) {

            if (slot.getSlotId().equalsIgnoreCase(newSlot.getSlotId())) {
                throw new IllegalArgumentException(
                        "A slot with this ID already exists."
                );
            }
        }

        slots.add(newSlot);
    }

    /**
     * Parks a vehicle and returns its new ticket.
     * Rules:
     *  - The same plate number is already parked -> throw VehicleAlreadyParkedException.
     *  - Choose the SMALLEST free slot that fits the vehicle.
     *    If two slots have the same size, use the one that was added first.
     *  - No free slot fits -> throw ParkingFullException.
     *  - Ticket IDs are "T1001", "T1002", "T1003" ...
     */
    public Ticket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) throws ParkingException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }

        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time cannot be null.");
        }

        // Check whether the same vehicle is already parked.
        for (Ticket ticket : activeTickets) {

            if (ticket.getVehicle().getPlateNumber().equalsIgnoreCase(vehicle.getPlateNumber())) {
                throw new VehicleAlreadyParkedException("This vehicle is already parked.");
            }
        }
        ParkingSlot selectedSlot = null;

        for (SlotType size : SlotType.values()) {

            if (size.ordinal() < vehicle.getRequiredSlotType().ordinal()) {
                continue;
            }

            for (ParkingSlot slot : slots) {

                if (slot.isAvailable()
                        && slot.getSlotType() == size
                        && slot.getSlotType().canFit(
                        vehicle.getRequiredSlotType())) {

                    selectedSlot = slot;
                    break;
                }
            }

            if (selectedSlot != null) {
                break;
            }
        }

        if (selectedSlot == null) {
            throw new ParkingFullException("No available parking slot fits this vehicle.");
        }

        String ticketId = "T" + ticketCounter;

        Ticket ticket = new Ticket(
                ticketId,
                vehicle,
                selectedSlot,
                entryTime
        );

        selectedSlot.park(vehicle);
        activeTickets.add(ticket);
        ticketCounter++;

        return ticket;
    }

    /**
     * The vehicle leaves. Returns the fee to pay.
     * Rules:
     *  - No active ticket with this ID (ignore capital/small letters) -> throw TicketNotFoundException.
     *  - Work out the charged hours, then the fee for this vehicle.
     *  - Free the slot, close the ticket, move it to the history, add the fee to the earnings.
     */
    public double exitVehicle(String ticketId, LocalDateTime exitTime) throws TicketNotFoundException {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new TicketNotFoundException("Ticket ID cannot be empty.");
        }

        Ticket selectedTicket = null;

        for (Ticket ticket : activeTickets) {

            if (ticket.getTicketId().equalsIgnoreCase(ticketId.trim())) {
                selectedTicket = ticket;
                break;
            }
        }

        if (selectedTicket == null) {
            throw new TicketNotFoundException("Ticket not found or already completed.");
        }

        long hours = calculateChargedHours(
                selectedTicket.getEntryTime(),
                exitTime
        );

        double fee = selectedTicket.getVehicle().calculateFee(hours);
        selectedTicket.getSlot().removeVehicle();
        selectedTicket.close(exitTime, fee);
        activeTickets.remove(selectedTicket);
        completedTickets.add(selectedTicket);
        totalEarnings += fee;
        return fee;

    }

    /**
     * Number of hours to charge.
     * Rules: round UP to the next full hour, minimum 1 hour.
     *        59 minutes = 1 hour, 60 minutes = 1 hour, 61 minutes = 2 hours.
     *        Exit time before entry time -> throw IllegalArgumentException.
     */
    public static long calculateChargedHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null) {
            throw new IllegalArgumentException("Entry time and exit time cannot be null.");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time.");
        }
        Duration duration = Duration.between(entryTime, exitTime);

        long seconds = duration.getSeconds();
        long hours = (seconds + 3599) / 3600;
        return Math.max(1, hours);
    }

    /**
     * Finds the active ticket of a parked vehicle (ignore capital/small letters).
     * Rule: not found -> throw TicketNotFoundException.
     */
    public Ticket findTicketByPlate(String plateNumber) throws TicketNotFoundException {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new TicketNotFoundException("Plate number cannot be empty.");
        }

        for (Ticket ticket : activeTickets) {
            if (ticket.getVehicle().getPlateNumber().equalsIgnoreCase(plateNumber.trim())) {
                return ticket;
            }
        }

        throw new TicketNotFoundException("No active ticket found for this plate number.");
    }

    /** All slots, in the order they were added. */
    public List<ParkingSlot> getAllSlots() {
        return new ArrayList<>(slots);
    }

    /** Only the free slots. */
    public List<ParkingSlot> getAvailableSlots() {
        List<ParkingSlot> available = new ArrayList<>();

        for (ParkingSlot slot : slots) {

            if (slot.isAvailable()) {
                available.add(slot);
            }
        }

        return available;
    }

    public int getAvailableSlotCount() {
        return getAvailableSlots().size();
    }

    /** Tickets of vehicles that are parked right now. */
    public List<Ticket> getActiveTickets() {
        return new ArrayList<>(activeTickets);
    }

    /** Closed tickets, NEWEST exit time first. */
    public List<Ticket> getCompletedTickets() {
        List<Ticket> history = new ArrayList<>(completedTickets);
        history.sort(Comparator.comparing(Ticket::getExitTime).reversed());
        return history;
    }

    /** Total of all fees paid so far. */
    public double getTotalEarnings() {
        return totalEarnings;
    }

    /**
     * BONUS TASK: save the completed tickets to a CSV file that opens in Excel.
     * If you skip the bonus, leave this method as it is.
     */
    public void exportHistoryToCsv(String filePath) throws IOException {
        throw new UnsupportedOperationException("CSV export is not written yet.");
    }
}
