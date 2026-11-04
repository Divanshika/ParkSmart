package com.parksmart.model;

/**
 * A van: Rs. 150 per hour, needs a LARGE slot.
 * Vans also pay a one-time service charge of Rs. 100 on top of the hourly fee.
 */
public class Van extends Vehicle {

    public Van(String plateNumber, String ownerName) {
        super(plateNumber, ownerName);
    }

    @Override
    public String getType() {
        return "Van";
    }

    @Override
    public double getHourlyRate() {
        return 150.0;
    }

    @Override
    public SlotType getRequiredSlotType() {
        return SlotType.LARGE;
    }

    @Override
    public double calculateFee(long hours) {

        if (hours < 1) {
            throw new IllegalArgumentException(
                    "Parking hours must be at least one hour."
            );
        }

        return (hours * getHourlyRate()) + 100.0;
    }
}
