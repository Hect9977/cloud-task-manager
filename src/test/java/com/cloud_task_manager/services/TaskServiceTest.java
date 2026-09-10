package com.cloud_task_manager.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloud_task_manager.exception.TaskNotFoundException;
import com.cloud_task_manager.model.Task;
import com.cloud_task_manager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    void getAllTasksReturnsRepositoryTasks() {
        Task task = createTask("Test task");
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void getTaskByIdReturnsExistingTask() {
        Task task = createTask("Existing task");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertSame(task, result);
    }

    @Test
    void getTaskByIdThrowsWhenTaskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void saveTaskCreatesNewTask() {
        Task task = createTask("New task");
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.saveTask(task);

        assertSame(task, result);
        verify(taskRepository).save(task);
    }

    @Test
    void saveTaskUpdatesExistingTask() {
        Task task = createTask("Updated task");
        task.setId(7L);
        when(taskRepository.existsById(7L)).thenReturn(true);
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.saveTask(task);

        assertSame(task, result);
        verify(taskRepository).save(task);
    }

    @Test
    void saveTaskRejectsMissingExistingTask() {
        Task task = createTask("Missing task");
        task.setId(7L);
        when(taskRepository.existsById(7L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.saveTask(task));
        verify(taskRepository, never()).save(task);
    }

    @Test
    void deleteTaskDeletesExistingTask() {
        when(taskRepository.existsById(3L)).thenReturn(true);

        taskService.deleteTask(3L);

        verify(taskRepository).deleteById(3L);
    }

    @Test
    void deleteTaskRejectsMissingTask() {
        when(taskRepository.existsById(3L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(3L));
        verify(taskRepository, never()).deleteById(3L);
    }

    private Task createTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Test description");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setStatus("Pending");
        return task;
    }
}
