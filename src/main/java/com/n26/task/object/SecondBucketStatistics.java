package com.n26.task.object;

/**
 * Created by prateekjassal on 16/9/17.
 */


/**
 * Holds statistics for a second
 *
 * count - Total number of transactions done
 * sum - Total amount of transactions done
 * min - Minimum transaction amount
 * max - Maximum transaction amount
 * average - Average transaction amount
 *
 */
public class SecondBucketStatistics {
    private long count;
    private double sum;
    private double min;
    private double max;
    private double average;


    public SecondBucketStatistics(){
        this.count = 0;
        this.sum = 0;
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MIN_VALUE;
        updateAverage();
    }

    public SecondBucketStatistics(SecondBucketStatistics obj) {
        this.count = obj.count;
        this.max = obj.max;
        this.min = obj.min;
        this.sum = obj.sum;
        updateAverage();
    }


    public SecondBucketStatistics(SecondBucketStatistics s1, SecondBucketStatistics s2) {
        this.max = Math.max(s1.max, s2.max);
        this.min = Math.min(s1.min, s2.min);
        this.count = s1.count+s2.count;
        this.sum = s1.sum+s2.sum;
        updateAverage();
    }


    /**
     * Resets statistics for this interval object so it maybe reused
     *
     * Think about how to make sure read and write are consistent and you don't end up reading data in the middle of a write
     *
     */
    public void reset() {
        this.count = 0;
        this.sum = 0;
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MIN_VALUE;
        updateAverage();
    }

    public long getCount() {
        return count;
    }

    private void incrementCount() {
        count++;
    }

    public double getSum() {
        return sum;
    }

    private void updateSum(double transactionAmount) {
        sum = sum + transactionAmount;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    private void updateMin(double min) {
        if(min < this.min)
            this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    private void updateMax(double max) {
        if(max > this.max)
            this.max = max;
    }

    public double getAverage() {
        return average;
    }

    /**
     * Returns average transaction amount for this interval
     * @return
     */
    public void updateAverage() {
        if(count == 0)
            this.average = 0;
        else
            this.average= sum/count;
    }

    /**
     * Updates the following statistics for the second
     * 1. Sum amount
     * 2. Average amount
     * 3. Min amount
     * 4. Max amount
     * 5. Transaction count
     * @param transactionAmount
     */
    public void updateStatisticsWithTransaction(double transactionAmount) {
        this.updateSum(transactionAmount);
        this.updateMin(transactionAmount);
        this.updateMax(transactionAmount);
        this.incrementCount();
        this.updateAverage();
    }

    public void accumulateStatistics(SecondBucketStatistics statistics) {
        this.sum += statistics.sum;
        this.count += statistics.count;
        this.max = Math.max(max, statistics.max);
        this.min = Math.min(min, statistics.min);
        updateAverage();
    }


    @Override public String toString() {
        return "SecondStatistics{" + "count=" + count + ", sum=" + sum + ", min=" + min + ", max="
            + max + ", average=" + average + '}';
    }
}
