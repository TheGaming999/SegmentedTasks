# SegmentedTasks
Recode of BukkitWorker. Basically, [this](https://www.spigotmc.org/threads/guide-on-workload-distribution-or-how-to-handle-heavy-splittable-tasks.409003/) but easier to use, and with some additions.
## Usage
Simply, schedule a task, and then choose which way you want to run it, ASYNC or SYNC. Both won't cause the server TPS to drop. However, if async is possible, you better of use it for better performance.  
  
Example:  
```java
RegularTask regularTask = SegmentedTasks.scheduleRegularTask();
// Since we will be using commands (which are not thread safe), we should use .init() instead of .initAsync()
regularTask.init();
```
Now we can use .run(Runnable runnable) method to run non-thread safe methods without choking the server.  
```java
regularTask.run(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give * DIAMOND 1"));
regularTask.run(() -> setBlockSomethingSomething());
```
## Task Types
#### Regular Task
Used for tasks that require speed and not dependant on a certain object type, meaning any task can be run with it.
```java
SegmentedTasks.scheduleRegularTask();
```
#### Distributed Task
Mainly used for tasks that don't require speed (not a priority), or don't directly affect game experience, such as: player data saving, database queries, or auto something...  
   
A task in which actions of inserted values into it are spread into different ticks. By distributing the values actions across several ticks, tasks won't clog a single tick.
```java
SegmentedTasks.scheduleDistributedTask(Consumer<T> action, Predicate<T> valueEscape)
```
#### Concurrent Task
Used for tasks that require speed and concurrency (which RegularTask lacks). 
```java
SegmentedTasks.scheduleConcurrentTask(Consumer<T> action, Predicate<T> valueEscape, Consumer<T> escapeAction)
```
Example usage:  
The purpose of this task is kill players gradually by subtracting 0.1 from their health as fast as possible.  
```java
// Create the task.
// Reduce player health
// IF player health is 0 / they are dead
// Send death message
ConcurrentTask<Player> deathTask = SegmentedTasks.scheduleConcurrentTask(player -> player.setHealth(player.getHealth() - 0.1d), player -> player.getHealth() == 0, player -> player.sendMessage("You died"));
// Start task
deathTask.init();
// Use the task
// Say it is from an event
deathTask.addValue(() -> event.getPlayer());
// You can use that a million times and it will never lag the server.
```
