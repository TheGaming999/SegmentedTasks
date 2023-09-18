package me.segmentedtasks;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SegmentedTasks {

    /**
     * Schedules a task in which actions of inserted values into this distributed task are spread into different ticks as long as the task is running.
     * By distributing the values actions across several ticks,
     * tasks won't stress a single tick.
     *
     * @param action      action to be applied on each value when reached
     * @param valueEscape condition for a value to be removed from the distributed task
     * @param <T>         type of values
     * @return DistributedTask, which you can start running by using one of init() methods.
     */
    public static <T> DistributedTask<T> scheduleDistributedTask(Consumer<T> action, Predicate<T> valueEscape) {
        return new DistributedTask<>(action, valueEscape, 20);
    }

    /**
     * Schedules a task in which actions of inserted values into this distributed task are spread into different ticks as long as the task is running.
     * By distributing the values actions across several ticks,
     * tasks won't stress a single tick.
     *
     * @param action           action to be applied on each value when reached
     * @param valueEscape      condition for a value to be removed from the distributed task
     * @param distributionSize cycle in how many ticks, (normally 20)
     * @param <T>              type of values
     * @return TypedDistributedTask, which you can start running by using one of init() methods.
     */
    public static <T> DistributedTask<T> scheduleDistributedTask(Consumer<T> action, Predicate<T> valueEscape, int distributionSize) {
        return new DistributedTask<>(action, valueEscape, distributionSize);
    }

    /**
     * Schedules a task in which it processes as many Workloads every tick as the given
     * field maxMillisPerTick allows. (normally: 2.5)
     *
     * @return RegularTask, which you can start running by using one of init() methods.
     */
    public static RegularTask scheduleRegularTask() {
        return new RegularTask();
    }

    /**
     * Schedules a task in which it processes as many Workloads every tick as the given
     * parameter maxMillisPerTick allows.
     *
     * @param maxMillisPerTick determines how many workloads can be processed in a tick (normally: 2.5)
     * @return RegularTask, which you can start running by using one of init() methods.
     */
    public static RegularTask scheduleRegularTask(double maxMillisPerTick) {
        return new RegularTask(maxMillisPerTick);
    }

    /**
     * Allows for multiple tasks to be executed without concurrency issues i.e. task doesn't affect other task. (Unlike regular task).
     *
     * @param action       action to be applied on each value when reached
     * @param valueEscape  condition for a value to be removed from the task
     * @param escapeAction action to be executed when value has escaped
     * @param <T>          type of values
     * @return ConcurrentTask, which you can start running by using one of init() methods.
     */
    public static <T> ConcurrentTask<T> scheduleConcurrentTask(Consumer<T> action, Predicate<T> valueEscape, Consumer<T> escapeAction) {
        return new ConcurrentTask<>(action, valueEscape, escapeAction, 20);
    }


    /**
     * Allows for multiple tasks to be executed without concurrency issues i.e. task doesn't affect other task.
     *
     * @param action           action to be applied on each value when reached
     * @param valueEscape      condition for a value to be removed from the task
     * @param escapeAction     action to be executed when value has escaped
     * @param maxMillisPerTick determines how many workloads can be processed in a tick. (normally: 2.5)
     * @param <T>              type of values
     * @return ConcurrentTask, which you can start running by using one of init() methods.
     */
    public static <T> ConcurrentTask<T> scheduleConcurrentTask(Consumer<T> action, Predicate<T> valueEscape, Consumer<T> escapeAction, double maxMillisPerTick) {
        return new ConcurrentTask<>(action, valueEscape, escapeAction, 20, maxMillisPerTick);
    }

}
