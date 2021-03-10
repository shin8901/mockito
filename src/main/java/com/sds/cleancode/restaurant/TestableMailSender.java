package com.sds.cleancode.restaurant;

public class TestableMailSender extends MailSender {
    private int sendMethodCallCount;

    @Override
    public void sendMail(Schedule schedule) {
        sendMethodCallCount++;
    }

    public int getSendMethodCallCount() {
        return sendMethodCallCount;
    }
}
