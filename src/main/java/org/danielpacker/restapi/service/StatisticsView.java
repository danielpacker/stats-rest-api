package org.danielpacker.restapi.service;

import java.util.DoubleSummaryStatistics;

// This class is a simple POJO for display in the controller.
// It is updated by the Statistics singleton.
public class StatisticsView {

    private long count = 0;
    private double sum = 0.0;
    private double avg = 0.0;
    private double min = 0.0;
    private double max = 0.0;

    public StatisticsView() {
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    // Convert the stats summary to stats for display.
    public void refresh(DoubleSummaryStatistics totalStats) {
        setSum(totalStats.getSum());
        setCount(totalStats.getCount());
        setAvg(totalStats.getAverage());
        // When this renders to JSON, Infinity should be represented as 0.
        if (totalStats.getMin() == Double.POSITIVE_INFINITY)
            setMin(0);
        else
            setMin(totalStats.getMin());
        if (totalStats.getMax() == Double.NEGATIVE_INFINITY)
            setMax(0);
        else
            setMax(totalStats.getMax());
    }
}
