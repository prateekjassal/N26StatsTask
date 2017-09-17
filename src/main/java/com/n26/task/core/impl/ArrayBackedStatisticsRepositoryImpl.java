package com.n26.task.core.impl;

import com.n26.task.constants.Constants;
import com.n26.task.core.IStatisticsRepository;
import com.n26.task.object.SecondBucketStatistics;
import org.springframework.stereotype.Repository;

/**
 * Created by prateekjassal on 17/9/17.
 */

@Repository("arrayRepo")
public class ArrayBackedStatisticsRepositoryImpl implements IStatisticsRepository {
    private SecondBucketStatistics secondBuckets[];
    private SecondBucketStatistics aggregatedStatistics;

    public ArrayBackedStatisticsRepositoryImpl() {
        aggregatedStatistics = new SecondBucketStatistics();
        secondBuckets = new SecondBucketStatistics[Constants.INTERVAL];
        for(int i=0;i<secondBuckets.length;i++)
            secondBuckets[i] = new SecondBucketStatistics();
    }


    /**
     *
     * Updates a second slot for the given transaction data and then updates the aggregated statistics
     *
     * @param index
     * @param amount
     */
    public synchronized void addTransactionStatistics(int index, double amount) {
        // Find target second slot's existing stats
        SecondBucketStatistics targetSlotStatistics = secondBuckets[index];
        // Update these statistics by adding the new amount
        targetSlotStatistics.updateStatisticsWithTransaction(amount);
        aggregatedStatistics.accumulateStatistics(targetSlotStatistics);
    }

    /**
     * Clears the statistics for a second slot and then recalculates the aggregated statistics
     * @param index
     */
    public synchronized void clearStatistics(int index) {
        secondBuckets[index].reset();
        SecondBucketStatistics newAggregate = new SecondBucketStatistics();
        for(int i=0;i<Constants.INTERVAL;i++) {
            if(secondBuckets[i].getCount() == 0)
                continue;
            SecondBucketStatistics currSlotStatistics = secondBuckets[i];
            newAggregate.accumulateStatistics(currSlotStatistics);
        }
        aggregatedStatistics = newAggregate;
    }

    /**
     * Reads the aggregated statistics
     * @return
     */
    public synchronized SecondBucketStatistics readAggregatedStatistics() {
        return new SecondBucketStatistics(aggregatedStatistics);
    }

}
