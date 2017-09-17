package com.n26.task.controller;

import com.n26.task.object.SecondBucketStatistics;
import com.n26.task.object.TransactionData;
import com.n26.task.service.IStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Created by prateekjassal on 17/9/17.
 */


/**
 * Transactions controller
 *
 * 1. Post a new transaction with timestamp
 * 2. Fetch statistics for the current timestamp
 */
@Controller
@RequestMapping("/v1")
public class TransactionController {
    private @Autowired IStatisticsService statisticsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity postTransaction(@RequestBody TransactionData transactionData) {
        long timestamp = System.currentTimeMillis();
        long diff = timestamp - transactionData.getTimestamp();
        // A transaction with a future timestamp has been posted
        if(diff < 0) {
            LOGGER.error("Rejecting the request as it refers to a future timestamp");
            return ResponseEntity.badRequest().build();
        } else if(diff > 60*1000) {
            LOGGER.warn("Passing the request as it refers to an interval before 1 minute");
            // For a transaction older than 60 seconds
            return ResponseEntity.noContent().build();
        }
        LOGGER.info("Processing request: {}", transactionData);
        statisticsService.updateStatistics(transactionData);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<SecondBucketStatistics> fetchStats() {
        long timestamp = System.currentTimeMillis();
        SecondBucketStatistics secondBucketStatistics = statisticsService.readCurrentStatistics();
        if(secondBucketStatistics.getCount()  == 0) {
            secondBucketStatistics.setMax(0);
            secondBucketStatistics.setMin(0);
        }
        LOGGER.info("Statistics for timestamp {} : {}",timestamp, secondBucketStatistics);
        return ResponseEntity.status(HttpStatus.OK).body(secondBucketStatistics);
    }

}
