package org.danielpacker.restapi.service;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Singleton static cache to hold real time statistics.
public class Statistics {

    private static final Logger log = LoggerFactory.getLogger(Statistics.class);

    // Re-use the POJO object that we pass to the controller.
    private static StatisticsView statsView = new StatisticsView();

    // Modifying REFRESH_RATE_MS will generate fewer or more buckets.
    static final int REFRESH_RATE_MS = 1000; // used by StatisticsTicker
    private static final int TIME_PERIOD_MS = 60*1000;
    private static final int MAX_BUCKETS = TIME_PERIOD_MS / REFRESH_RATE_MS;

    // All code that accesses this is synchronized.
    private static ArrayList<DoubleSummaryStatistics> timeBuckets = new ArrayList<>(MAX_BUCKETS);

    // Singleton instance loaded up front.
    private static final Statistics INSTANCE = new Statistics();


    // This is only ever used by the class itself.
    private Statistics() {

        // populate buckets with clean stats
        log.info("Initializing statistics...");

        // Paranoia about initialization because of Spring introspection.
        if (timeBuckets.size()==0)
            for (int i=0; i < MAX_BUCKETS; i++)
                timeBuckets.add(new DoubleSummaryStatistics());
    }

     // Add a transaction to a time bucket to count it.
     public static boolean addTran(Transaction t) {

        if (t.hasValidTimestamp()) {
            int bucket = bucketForTran(t);

            // Avoid updating one bucket (DSS) simultaneously
            synchronized (Statistics.class) {
                timeBuckets.get(bucket).accept(t.getAmount());
            }
            return true;
        }
        return false;
    }

    // Given a transaction timestamp, determine bucket index.
    private static int bucketForTran(Transaction t) {

        // Calculate age of transaction as delta
        long current = System.currentTimeMillis();
        long delta = current - t.getTimestamp();

        // Convert delta to age ratio and scale by # of possible buckets
        // This will generate a number from 0..MAX_BUCKETS
        int bucketOffset = (int)(((double)delta / TIME_PERIOD_MS) * MAX_BUCKETS);

        // Use the generated offset to get the final bucket index
        return MAX_BUCKETS - (bucketOffset + 1);
    }

    // Called by the controller to display the stats via POJO.
    public static StatisticsView getStatsView() {
        return statsView;
    }

    // Refreshes the total stats and stats view by
    //   combining all time buckets into one summary.
    synchronized static void tickBuckets() {

        log.info("Another slice of time ticked by...");

        // Combine all bucket stats
        DoubleSummaryStatistics totalStats = new DoubleSummaryStatistics();
        for (int i=0; i < timeBuckets.size(); i++) {
            //System.out.println(i + ": " + timeBuckets.get(i));
            totalStats.combine(timeBuckets.get(i));
        }

        // Refresh visible stats
        statsView.refresh(totalStats);

        // Purge oldest bucket and add fresh one
        timeBuckets.remove(0);
        timeBuckets.add(new DoubleSummaryStatistics());
    }

    // Useful for testing.
    public synchronized static void clear() {

        log.info("Clearing statistics...");

        for (int i=0; i < timeBuckets.size(); i++)
            timeBuckets.set(i, new DoubleSummaryStatistics());

        // Count those empty buckets.
        tickBuckets();
    }

}
