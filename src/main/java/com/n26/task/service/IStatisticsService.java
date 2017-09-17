package com.n26.task.service;

import com.n26.task.object.SecondBucketStatistics;
import com.n26.task.object.TransactionData;

/**
 * Created by prateekjassal on 16/9/17.
 */


/**
 * Interface for the statistics service allowing
 * 1. Update the current minute statistics by adding a transaction
 * 2. Read the current minute statistics
 * 3. Clear the statistics for a second slot by the job
 */
public interface IStatisticsService {
    void clearStatistics(int index);
    void updateStatistics(TransactionData transactionData);
    SecondBucketStatistics readCurrentStatistics();
}
