package com.parksmart.model;

/** A car: Rs. 100 per hour, needs a MEDIUM slot. */
public class Car extends Vehicle {

    public Car(String plateNumber, String ownerName) {
        super(plateNumber, ownerName);
    }

    @Override
    public String getType() {
        return "Car";
    }

    @Override
    public double getHourlyRate() {
        return 100.0;
    }

    @Override
    public SlotType getRequiredSlotType() {
        return SlotType.MEDIUM;
    }
}
