package com.n26.task.job;

import com.n26.task.constants.Constants;
import com.n26.task.service.IStatisticsService;
import com.n26.task.utils.Utils;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by prateekjassal on 17/9/17.
 */
@Service public class SecondBucketClearingJob {

    private static final Logger LOGGER = LoggerFactory.getLogger("job");
    @Autowired private IStatisticsService statisticsService;
    private SimpleDateFormat CURRENT_TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");

    /**
     * Runs at the start of every second to reset the segment tree bucket for that second
     */
    @Scheduled(cron = "* * * * * *") public void execute() {
        MDC.put(Constants.JOB_NAME_LOG_KEY, Constants.SECOND_BUCKET_CLEARING_JOB_NAME);
        long timestamp = System.currentTimeMillis();
        int index = Utils.getSecondSlot(Utils.roundOffToSeconds(timestamp));
        LOGGER.info("Running job timestamp:{}({}) on second bucket index {} ", timestamp,
            CURRENT_TIME_FORMATTER.format(new Date(timestamp)), index);
        statisticsService.clearStatistics(index);
        MDC.remove(Constants.JOB_NAME_LOG_KEY);
    }
}
