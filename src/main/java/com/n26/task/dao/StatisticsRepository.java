package com.n26.task.dao;

import com.n26.task.object.SecondBucketStatistics;
import com.n26.task.utils.MinuteStatisticsSegmentTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by prateekjassal on 16/9/17.
 */


/**
 * A wrapper over the underlying segment tree which is the actual datastore
 * for all statistics
 *
 */

@Repository
public class StatisticsRepository {
    private @Autowired MinuteStatisticsSegmentTree minuteStatisticsSegmentTree;

    /**
     * Get statistics for the last minute
     * @return
     */
    public SecondBucketStatistics read() {
        return minuteStatisticsSegmentTree.getAggregateStatistics();
    }

    public void updateIntervalStatistics(int index, double transactionAmount, boolean isJob) {
        minuteStatisticsSegmentTree.update(index, transactionAmount, isJob);
    }

}
