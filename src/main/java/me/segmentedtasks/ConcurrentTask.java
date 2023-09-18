package me.segmentedtasks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConcurrentTask<T> implements SegmentedTask {

    private final Consumer<T> action;
    private final Predicate<T> escapeCondition;
    private final Consumer<T> escapeAction;
    private final List<Supplier<T>> suppliedValues;
    private final int distributionSize;
    private int currentPosition = 0;
    private int addedValues = 0;
    private static final double MAX_MILLIS_PER_TICK = 2.5;
    private int maxNanosPerTick = (int) (MAX_MILLIS_PER_TICK * 1E6);

    public ConcurrentTask(Consumer<T> action, Predicate<T> escapeCondition, Consumer<T> escapeAction, int distributionSize) {
        this(action, escapeCondition, escapeAction, distributionSize, 2.5);
    }

    public ConcurrentTask(Consumer<T> action, Predicate<T> escapeCondition, Consumer<T> escapeAction, int distributionSize, double maxMillisecondsPerTick) {
        this.escapeAction = escapeAction;
        this.distributionSize = distributionSize;
        this.action = action;
        this.escapeCondition = escapeCondition;
        this.suppliedValues = new ArrayList<>(distributionSize);
        maxNanosPerTick = (int) (maxMillisecondsPerTick * 1E6);
    }

    public void addValue(Supplier<T> valueSupplier) {
        suppliedValues.add(valueSupplier);
        addedValues++;
    }

    private void proceedPosition() {
        if (this.currentPosition >= addedValues - 1) {
            this.currentPosition = 0;
        } else {
            this.currentPosition++;
        }
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime() + maxNanosPerTick;
        while (System.nanoTime() <= stopTime && addedValues != 0) {
            executeThenGet();
            proceedPosition();
        }
    }

    private synchronized Supplier<T> executeThenGet() {
        if (suppliedValues.isEmpty()) return null;
        Supplier<T> supplier = suppliedValues.get(currentPosition);
        if (supplier == null) return null;
        boolean removed = executeThenCheck(supplier);
        if (removed) {
            addedValues--;
            escapeAction.accept(supplier.get());
            suppliedValues.remove(currentPosition);
        }
        return removed ? null : supplier;
    }

    private synchronized boolean executeThenCheck(Supplier<T> valueSupplier) {
        T value = valueSupplier.get();
        this.action.accept(value);
        return this.escapeCondition.test(value);
    }

}
