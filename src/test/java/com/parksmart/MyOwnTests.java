package com.parksmart;

import com.parksmart.model.Car;
import com.parksmart.model.ParkingLot;
import com.parksmart.model.SlotType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===========================================================
 *  YOUR OWN TESTS - Part 4.
 *
 *  Write at least 3 tests for rules that ParkSmartTest does NOT check.
 *  For each test: remove the @Disabled line, give it a clear
 *  @DisplayName, and write the code.
 *
 *  Pattern:  1. set up objects   2. do the action   3. assert the result
 * ===========================================================
 */
class MyOwnTests {
    // Test 1: An empty owner name should be accepted
    @Test
    @DisplayName("My test 1: Empty owner name is rejected\"")
    void myTest1() {
        assertThrows(IllegalArgumentException.class, () -> new Car("CAB-1234", " "));
    }

    //Test 2: The parking lot should reject a null vehicle
    @Test
    @DisplayName("My test 2: Null vehicle is rejected")
    void myTest2() throws Exception {

        ParkingLot lot = new ParkingLot("Test Parking");

        lot.addSlot("M1", SlotType.MEDIUM);

        assertThrows(IllegalArgumentException.class, () -> lot.parkVehicle(
                null, LocalDateTime.of(2026, 11, 2, 9, 0)));
    }
    // Test 3:The parking lot should reject a null entry time.
    @Test
    @DisplayName("My test 3: Null entry time is rejected")
    void myTest3() {
        ParkingLot lot = new ParkingLot("Test Parking");

        lot.addSlot("M1", SlotType.MEDIUM);

        Car car = new Car("CAB-1234", "Nimal");

        assertThrows(IllegalArgumentException.class, () -> lot.parkVehicle(car, null));
    }
}
