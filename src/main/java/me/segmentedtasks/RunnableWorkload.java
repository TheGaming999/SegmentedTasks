package me.segmentedtasks;

class RunnableWorkload implements Workload {

    private Runnable runnable;

    public RunnableWorkload(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public boolean compute() {
        runnable.run();
        return true;
    }

}
