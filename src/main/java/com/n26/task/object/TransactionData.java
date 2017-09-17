package com.n26.task.object;

/**
 * Created by prateekjassal on 17/9/17.
 */
public class TransactionData {
    private double amount;
    private long timestamp;

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

    @Override public String toString() {
        return "TransactionData{" + "amount=" + amount + ", timestamp=" + timestamp + '}';
    }
}
