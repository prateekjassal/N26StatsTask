package com.n26.task.core.impl;

import com.n26.task.constants.Constants;
import com.n26.task.core.IStatisticsRepository;
import com.n26.task.object.SecondBucketStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by prateekjassal on 16/9/17.
 */


/**
 *
 * A segment tree with the root node containing statistics for the last 1 minute
 * Has 61 leaves, each leaf containing the statistics for a second
 * Each leaf represents a bucket containing statistics for that second
 * An extra leaf has been used as the SecondBucketClearingJob runs every second to reuse the
 * oldest second bucket which has passed the 1 minute mark and resets it.
 * So this won't affect the last minute results
 *
 *
 * Traditional segment trees have another array which stores the original values
 * but in this case the leaf nodes will always contain the original values i.e.
 * the statistics for the last 61 seconds hence no additional array is required
 *
 */
@Repository("segRepo")
public class SegmentTreeBackedStatisticsRepositoryImpl implements IStatisticsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        SegmentTreeBackedStatisticsRepositoryImpl.class);
    private SecondBucketStatistics tree[];

    private SegmentTreeBackedStatisticsRepositoryImpl() {
        int h = (int)(Math.ceil(Math.log(Constants.INTERVAL)/Math.log(2)));
        int size = 2* (int) Math.pow(2, h) - 1;
        tree = new SecondBucketStatistics[size];
        for(int i=0; i<size;i++)
            tree[i] = new SecondBucketStatistics();
    }

    /**
     * Returns a copy of the root node which contains the aggregated statistics
     * for the last minute
     * @return
     */
    @Override public synchronized SecondBucketStatistics readAggregatedStatistics() {
        // Return a copy so the original does not get modified
        return new SecondBucketStatistics(tree[0]);
    }

    @Override public synchronized void clearStatistics(int index) {
        resetOldestLeafAndUpdate(0, Constants.INTERVAL-1, index, 0);
    }

    @Override public synchronized void addTransactionStatistics(int index, double amount) {
        updateLeaf(0, Constants.INTERVAL-1, index, 0, amount);
    }



    /**
     *
     * Called by the cron SecondBucketClearingJob that runs every second
     * Resets the oldest leaf node's statistics
     * (oldest leaf is the second that just passed the 1 minute mark)
     * @param start - Segment tree array's first index
     * @param end - Segment tree array's last index
     * @param ai - Bucket index in terms of the interval i.e. (0-60)
     * @param si - Index of the current node in the segment tree
     */
    private void resetOldestLeafAndUpdate(int start, int end, int ai, int si) {
        // Leaf node reached, reset the stats
        if(start == end)
        {
            SecondBucketStatistics clone = new SecondBucketStatistics(tree[si]);
            clone.reset();
            tree[si] = clone;
        }
        else
        {
            // Non leaf node, move to either left child or right child based on the bucket index
            // Updates the leaf node first and then recursively updates the parents
            int mid = mid(start, end);
            if(ai>= start && ai<=mid) {
                resetOldestLeafAndUpdate(start, mid,  ai, 2*si+1);
            }
            else {
                resetOldestLeafAndUpdate(mid+1, end, ai, 2*si+2);
            }
            int left = left(si);
            int right = right(si);
            SecondBucketStatistics leftChild = tree[left];
            SecondBucketStatistics rightChild = tree[right];
            // Updates the parent by comparing the children as any one of them might have changed
            SecondBucketStatistics updatedParent = new SecondBucketStatistics(leftChild, rightChild);
            tree[si] = updatedParent;
        }
    }

    /**
     * Called to add a new transaction to a second's bucket
     *
     * @param start - Segment tree array's first index
     * @param end - Segment tree array's last index
     * @param ai - Bucket index in terms of the interval i.e. (0-60)
     * @param si - Index of the current node in the segment tree
     * @param transactionAmount - Amount of the transaction to be added
     */
    private void updateLeaf(int start, int end, int ai, int si, double transactionAmount) {
        LOGGER.debug("Range: "+start+" - "+end);
        // Leaf node
        if(start == end)
        {
            SecondBucketStatistics clone = new SecondBucketStatistics(tree[si]);
            clone.updateStatisticsWithTransaction(transactionAmount);
            tree[si] = clone;
            LOGGER.debug("Updated index: "+si+" - "+clone);
        }
        else
        {   // Updates current node stats
            int mid = mid(start, end);
            SecondBucketStatistics clone = new SecondBucketStatistics(tree[si]);
            clone.updateStatisticsWithTransaction(transactionAmount);
            tree[si] = clone;
            LOGGER.debug("Updated index: "+si+" - "+clone);

            if(ai>= start && ai<=mid) {
                updateLeaf(start, mid,  ai, 2*si+1, transactionAmount);
            }
            else {
                updateLeaf(mid+1, end, ai, 2*si+2, transactionAmount);
            }
        }
    }

    /**
     * Returns the mid of start and end
     * @param start
     * @param end
     * @return
     */
    private int mid(int start, int end) {
        return start + (end - start) / 2;
    }

    /**
     * Returns left child of index
     * @param index
     * @return
     */
    private int left(int index) {
        return 2*index+1;
    }

    /**
     * Returns right child of index
     * @param index
     * @return
     */
    private int right(int index) {
        return 2*index+2;
    }

}
