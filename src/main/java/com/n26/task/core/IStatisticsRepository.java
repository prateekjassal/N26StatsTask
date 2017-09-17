package com.n26.task.core;

import com.n26.task.object.SecondBucketStatistics;

/**
 * Created by prateekjassal on 17/9/17.
 */
public interface IStatisticsRepository {
    SecondBucketStatistics readAggregatedStatistics();
    void clearStatistics(int index);
    void addTransactionStatistics(int index, double amount);

}
