package com.parksmart.model;

/** A bike: Rs. 50 per hour, needs a SMALL slot. */
public class Bike extends Vehicle {

    public Bike(String plateNumber, String ownerName) {
        super(plateNumber, ownerName);
    }

    @Override
    public String getType() {
        return "Bike";
    }

    @Override
    public double getHourlyRate() {
        return 50.0;
    }

    @Override
    public SlotType getRequiredSlotType() {
        return SlotType.SMALL;
    }
}
