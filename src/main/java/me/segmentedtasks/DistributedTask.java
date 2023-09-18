package me.segmentedtasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DistributedTask<T> implements SegmentedTask {

    private final Consumer<T> action;
    private final Predicate<T> escapeCondition;
    private final List<LinkedList<Supplier<T>>> suppliedValueMatrix;
    private final int distributionSize;
    private int currentPosition = 0;

    public DistributedTask(Consumer<T> action, Predicate<T> escapeCondition, int distributionSize) {
        this.distributionSize = distributionSize;
        this.action = action;
        this.escapeCondition = escapeCondition;
        this.suppliedValueMatrix = new ArrayList<>(distributionSize);
        for (int i = 0; i < distributionSize; i++) {
            this.suppliedValueMatrix.add(new LinkedList<>());
        }
    }

    public void addValue(Supplier<T> valueSupplier) {
        List<Supplier<T>> smallestList = this.suppliedValueMatrix.get(0);
        for (int index = 0; index < this.distributionSize; index++) {
            if (smallestList.size() == 0) {
                break;
            }
            List<Supplier<T>> next = this.suppliedValueMatrix.get(index);
            int size = next.size();
            if (size < smallestList.size()) {
                smallestList = next;
            }
        }
        smallestList.add(valueSupplier);
    }

    private void proceedPosition() {
        if (++this.currentPosition == this.distributionSize) {
            this.currentPosition = 0;
        }
    }

    @Override
    public void run() {
        this.suppliedValueMatrix.get(this.currentPosition).removeIf(this::executeThenCheck);
        this.proceedPosition();
    }

    private boolean executeThenCheck(Supplier<T> valueSupplier) {
        T value = valueSupplier.get();
        this.action.accept(value);
        return this.escapeCondition.test(value);
    }

}
