package pl.edu.pb.todoapp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskStorage {
    private static final TaskStorage taskStorage = new TaskStorage();
    private final List<Task> tasks;

    public static TaskStorage getInstance() {
        return taskStorage;
    }

    private TaskStorage() {
        tasks = new ArrayList<>();
        for (int i = 1; i <= 150; i++) {
            Task task = new Task();
            task.setName("Pilne zadanie numer " + i);
            task.setDone(i % 3 == 0);
            tasks.add(task);
        }
    }

    public List<Task> getTaskList() {
        return tasks;
    }

    public Task getTask(UUID taskId) {
        for(Task task:tasks){
            if(task.getId().equals(taskId)) {
                return task;
            }
        }
        //throw new IllegalArgumentException(String.format("There is no task for id '%s'", taskId));
        //return new Task();
        return null;
    }
}

