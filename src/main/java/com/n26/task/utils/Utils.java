package com.n26.task.utils;

import com.n26.task.constants.Constants;

/**
 * Created by prateekjassal on 16/9/17.
 */
public class Utils {
    /**
     * Rounds down the timestamp in milliseconds to seconds
     * @param timestampInMillis
     * @return
     */
    public static long roundOffToSeconds(long timestampInMillis) {
        return (long) Math.floor(timestampInMillis/1000);
    }


    /**
     * Returns the slot interval within the 61 second slot
     * @param timestampInSecs
     * @return
     */
    public static int getSecondSlot(long timestampInSecs) {
        return (int)timestampInSecs% Constants.INTERVAL;
    }


}
