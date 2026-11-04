package com.parksmart.exception;

/** Thrown when: No free slot fits the vehicle. */
public class ParkingFullException extends ParkingException {

    public ParkingFullException(String message) {
        super(message);
    }
}
