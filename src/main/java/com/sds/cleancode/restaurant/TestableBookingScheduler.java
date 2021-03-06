package com.sds.cleancode.restaurant;

import org.joda.time.DateTime;

public class TestableBookingScheduler extends BookingScheduler{
    private DateTime dateTime;

    public TestableBookingScheduler(int capacityPerHour, DateTime dateTime) {
        super(capacityPerHour);
        this.dateTime = dateTime;
    }

    @Override
    public DateTime getNow() {
        return dateTime;
    }
}
