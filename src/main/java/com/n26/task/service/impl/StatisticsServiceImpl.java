package com.n26.task.service.impl;

import com.n26.task.dao.StatisticsRepository;
import com.n26.task.object.SecondBucketStatistics;
import com.n26.task.object.TransactionData;
import com.n26.task.service.IStatisticsService;
import com.n26.task.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by prateekjassal on 16/9/17.
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {
    @Autowired private StatisticsRepository cache;
    private static final Logger LOGGER = LoggerFactory.getLogger("stats");


    @Override public void updateStatistics(TransactionData transactionData) {
        long timestampInSecs = Utils.roundOffToSeconds(transactionData.getTimestamp());
        int index = Utils.getSecondSlot(timestampInSecs);
        LOGGER.info("Second bucket index to be updated {} for amount {} ",index, transactionData.getAmount());
        cache.updateIntervalStatistics(index, transactionData.getAmount(), false);
    }

    @Override public SecondBucketStatistics readCurrentStatistics() {
        return cache.read();
    }
}
