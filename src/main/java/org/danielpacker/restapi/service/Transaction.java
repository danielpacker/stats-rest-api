package org.danielpacker.restapi.service;

public class Transaction {

    private double amount;
    private long timestamp;

    public Transaction(double amount, long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean hasValidTimestamp() {
        long current = System.currentTimeMillis();
        long delta = (current - timestamp) / 1000;
        //System.out.println("Current epoch millis: " + current);
        //System.out.println("timestamp: " + timestamp);
        //System.out.println("delta: " + delta);

        // Reject future and old (>1 min) timestamps
        return (delta >= 0 && delta <= 60);
    }
}
