package com.cloud_task_manager.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloud_task_manager.exception.TaskNotFoundException;
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
        logger.debug("Retrieving all tasks from the database");
        List<Task> tasks = taskRepository.findAll();
        logger.info("Retrieved {} task(s)", tasks.size());
        return tasks;
    }

    public Task getTaskById(Long id) {
        logger.debug("Looking up task with ID {}", id);

        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task with ID {} was not found", id);
                    return new TaskNotFoundException(id);
                });
    }

    public Task saveTask(Task task) {
        boolean isNewTask = task.getId() == null;

        if (!isNewTask && !taskRepository.existsById(task.getId())) {
            logger.warn("Cannot update missing task with ID {}", task.getId());
            throw new TaskNotFoundException(task.getId());
        }

        logger.info("{} task: id={}, title={}",
                isNewTask ? "Creating" : "Updating",
                task.getId(),
                task.getTitle());

        Task savedTask = taskRepository.save(task);
        logger.info("Task saved successfully with ID {}", savedTask.getId());
        return savedTask;
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            logger.warn("Cannot delete missing task with ID {}", id);
            throw new TaskNotFoundException(id);
        }

        logger.info("Deleting task with ID {}", id);
        taskRepository.deleteById(id);
        logger.info("Task with ID {} was deleted successfully", id);
    }
}
