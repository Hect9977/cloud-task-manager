package com.cloud_task_manager.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloud_task_manager.model.Task;
import com.cloud_task_manager.repository.TaskRepository;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        logger.info("Retrieving all tasks");
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        logger.info("Retrieving task with ID: {}", id);

        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public Task saveTask(Task task) {
        logger.info("Saving task: {}", task.getTitle());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        logger.info("Deleting task with ID: {}", id);
        taskRepository.deleteById(id);
    }
}
