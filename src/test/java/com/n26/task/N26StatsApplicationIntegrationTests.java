package com.n26.task;

import com.n26.task.bootup.N26StatsApplication;
import com.n26.task.controller.TransactionController;
import com.n26.task.object.SecondBucketStatistics;
import com.n26.task.object.TransactionData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) @ContextConfiguration(classes = N26StatsApplication.class)
public class N26StatsApplicationIntegrationTests {
    @Autowired private TransactionController transactionController;
    private static final Logger LOGGER = LoggerFactory.getLogger(N26StatsApplicationIntegrationTests.class);
    private static long SLEEP_TIME = 12000;
    private static long SEVENTY_SECS = 70000;

    /**
     * Integration test with 4 transactions added
     * Requests for statistics after each of them is expected to expire
     *
     * @throws InterruptedException
     */
    @Test public void positiveIntegrationTest() throws InterruptedException {
        long timestamp = System.currentTimeMillis();
        LOGGER.info(
            "Positive integration test, will take roughly 50 seconds as it involves waiting for older transactions to expire");
        // Insert a 50 seconds old transaction
        TransactionData t = new TransactionData();
        t.setAmount(10.0);
        t.setTimestamp(timestamp - 50000);
        Assert.assertEquals(HttpStatus.CREATED,
            transactionController.postTransaction(t).getStatusCode());

        // Insert a 40 seconds old transaction
        t = new TransactionData();
        t.setAmount(20.0);
        t.setTimestamp(timestamp - 40000);
        Assert.assertEquals(HttpStatus.CREATED,
            transactionController.postTransaction(t).getStatusCode());


        // Insert a 30 seconds old transaction
        t = new TransactionData();
        t.setAmount(30.0);
        t.setTimestamp(timestamp - 30000);
        Assert.assertEquals(HttpStatus.CREATED,
            transactionController.postTransaction(t).getStatusCode());


        // Insert a 20 seconds old transaction
        t = new TransactionData();
        t.setAmount(40.0);
        t.setTimestamp(timestamp - 20000);
        Assert.assertEquals(HttpStatus.CREATED,
            transactionController.postTransaction(t).getStatusCode());

        // Get statistics, should include all 4
        SecondBucketStatistics secondBucketStatistics =
            transactionController.fetchStats().getBody();
        LOGGER.info("Stats: {}", secondBucketStatistics);
        Assert.assertEquals(4, secondBucketStatistics.getCount());
        Assert.assertEquals(100, secondBucketStatistics.getSum(), 0.00);
        Assert.assertEquals(25, secondBucketStatistics.getAverage(), 0.00);
        Assert.assertEquals(40, secondBucketStatistics.getMax(), 0.00);
        Assert.assertEquals(10, secondBucketStatistics.getMin(), 0.00);

        // Sleep so that transaction 1 results have been removed by now
        Thread.sleep(SLEEP_TIME);

        // Get statistics, should include last 3
        secondBucketStatistics = transactionController.fetchStats().getBody();
        LOGGER.info("Stats: {}", secondBucketStatistics);
        Assert.assertEquals(3, secondBucketStatistics.getCount());
        Assert.assertEquals(90, secondBucketStatistics.getSum(), 0.00);
        Assert.assertEquals(30, secondBucketStatistics.getAverage(), 0.00);
        Assert.assertEquals(40, secondBucketStatistics.getMax(), 0.00);
        Assert.assertEquals(20, secondBucketStatistics.getMin(), 0.00);

        // Sleep so that transaction 2 results have been removed by now
        Thread.sleep(SLEEP_TIME);

        // Get statistics, should include last 2
        secondBucketStatistics = transactionController.fetchStats().getBody();
        LOGGER.info("Stats: {}", secondBucketStatistics);
        Assert.assertEquals(2, secondBucketStatistics.getCount());
        Assert.assertEquals(70, secondBucketStatistics.getSum(), 0.00);
        Assert.assertEquals(35, secondBucketStatistics.getAverage(), 0.00);
        Assert.assertEquals(40, secondBucketStatistics.getMax(), 0.00);
        Assert.assertEquals(30, secondBucketStatistics.getMin(), 0.00);

        // Sleep so that transaction 3 results have been removed by now
        Thread.sleep(SLEEP_TIME);

        // Get statistics, should include last 1
        secondBucketStatistics = transactionController.fetchStats().getBody();
        LOGGER.info("Stats: {}", secondBucketStatistics);
        Assert.assertEquals(1, secondBucketStatistics.getCount());
        Assert.assertEquals(40, secondBucketStatistics.getSum(), 0.00);
        Assert.assertEquals(40, secondBucketStatistics.getAverage(), 0.00);
        Assert.assertEquals(40, secondBucketStatistics.getMax(), 0.00);
        Assert.assertEquals(40, secondBucketStatistics.getMin(), 0.00);

        // Sleep so that transaction 4 results have been removed by now
        Thread.sleep(SLEEP_TIME);

        // Get statistics, should include none
        secondBucketStatistics = transactionController.fetchStats().getBody();
        LOGGER.info("Stats: {}", secondBucketStatistics);
        Assert.assertEquals(0, secondBucketStatistics.getCount());
        Assert.assertEquals(0, secondBucketStatistics.getSum(), 0.00);
        Assert.assertEquals(0, secondBucketStatistics.getAverage(), 0.00);
        Assert.assertEquals(0, secondBucketStatistics.getMax(), 0.00);
        Assert.assertEquals(0, secondBucketStatistics.getMin(), 0.00);
    }


    @Test public void noContentIntegrationTest() throws InterruptedException {
        // Should return 204
        TransactionData t = new TransactionData();
        t.setAmount(100);
        t.setTimestamp(System.currentTimeMillis() - SEVENTY_SECS);
        Assert.assertEquals(HttpStatus.NO_CONTENT,
            transactionController.postTransaction(t).getStatusCode());
    }

    @Test public void badRequestIntegrationTest() throws InterruptedException {
        // Should return 400
        TransactionData t = new TransactionData();
        t.setAmount(100);
        t.setTimestamp(System.currentTimeMillis() + SEVENTY_SECS);
        Assert.assertEquals(HttpStatus.BAD_REQUEST,
            transactionController.postTransaction(t).getStatusCode());
    }

    @Test public void badRequestWithNegativeAmountIntegrationTest() throws InterruptedException {
        // Should return 400
        TransactionData t = new TransactionData();
        t.setAmount(-0.1);
        t.setTimestamp(System.currentTimeMillis() + SEVENTY_SECS);
        Assert.assertEquals(HttpStatus.BAD_REQUEST,
            transactionController.postTransaction(t).getStatusCode());
    }
}
