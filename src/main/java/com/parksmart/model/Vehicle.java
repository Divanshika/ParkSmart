package com.parksmart.model;

/**
 * Parent class for every vehicle (Car, Bike, Van).
 * It is abstract because the car park never parks a "general" vehicle.
 */
public abstract class Vehicle {

    private String plateNumber;
    private String ownerName;

    /**
     * Creates a vehicle.
     * Rules: plate number and owner name cannot be empty -> throw IllegalArgumentException.
     *        Remove extra spaces. Save the plate number in capital letters.
     */
    public Vehicle(String plateNumber, String ownerName) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Plate number cannot be empty.");
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }
        this.plateNumber = plateNumber.trim().toUpperCase();

        this.ownerName = ownerName.trim();
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    /** Returns "Car", "Bike" or "Van". */
    public abstract String getType();

    /** Returns the price for one hour of parking, in rupees. */
    public abstract double getHourlyRate();

    /** Returns the smallest slot size this vehicle needs. */
    public abstract SlotType getRequiredSlotType();

    /**
     * Returns the fee for the given number of charged hours.
     * Rule: hours less than 1 -> throw IllegalArgumentException.
     */
    public double calculateFee(long hours) {
        if (hours < 1) {
            throw new IllegalArgumentException("Hours must be at least 1");
        }

        return hours * getHourlyRate();
    }

    @Override
    public String toString() {
        return getType() + " " + getPlateNumber() + " (" + getOwnerName() + ")";
    }
}
