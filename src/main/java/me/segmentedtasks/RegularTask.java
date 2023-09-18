package me.segmentedtasks;

import java.util.ArrayDeque;
import java.util.Deque;

public class RegularTask implements SegmentedTask {

    private static final double MAX_MILLIS_PER_TICK = 2.5;

    private int maxNanosPerTick = (int) (MAX_MILLIS_PER_TICK * 1E6);

    final Deque<Workload> workloadDeque;

    public RegularTask() {
        workloadDeque = new ArrayDeque<>();
    }

    public RegularTask(double maxMillisecondsPerTick) {
        workloadDeque = new ArrayDeque<>();
        maxNanosPerTick = (int) (maxMillisecondsPerTick * 1E6);
    }

    public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }

    public void run(Runnable runnable) {
        addWorkload(new RunnableWorkload(runnable));
    }

    public void clearWorkloads() {
        workloadDeque.clear();
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime() + maxNanosPerTick;

        Workload nextLoad;

        while (System.nanoTime() <= stopTime && (nextLoad = this.workloadDeque.poll()) != null) {
            if (!nextLoad.compute()) {
                clearWorkloads();
            }
        }
    }

}
