package com.cloud_task_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.cloud_task_manager.model.Task;
import com.cloud_task_manager.services.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    private TaskController taskController;

    @BeforeEach
    void setUp() {
        taskController = new TaskController(taskService);
    }

    @Test
    void homeRouteReturnsIndexView() {
        assertEquals("index", taskController.showHomePage());
    }

    @Test
    void taskListRouteAddsTasksToModel() {
        ExtendedModelMap model = new ExtendedModelMap();
        when(taskService.getAllTasks()).thenReturn(List.of(new Task()));

        String viewName = taskController.showTaskList(model);

        assertEquals("task-list", viewName);
        assertEquals(1, ((List<?>) model.get("tasks")).size());
    }

    @Test
    void validTaskFormSavesAndRedirects() {
        Task task = createValidTask();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(task, "task");
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        when(taskService.saveTask(task)).thenReturn(task);

        String viewName = taskController.saveTask(task, result, redirectAttributes);

        assertEquals("redirect:/tasks", viewName);
        assertEquals("Task created successfully.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
        verify(taskService).saveTask(task);
    }

    @Test
    void invalidTaskFormReturnsFormWithoutSaving() {
        Task task = createValidTask();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(task, "task");
        result.rejectValue("title", "title.required", "Title is required");

        String viewName = taskController.saveTask(
                task,
                result,
                new RedirectAttributesModelMap());

        assertEquals("task-form", viewName);
        verify(taskService, never()).saveTask(task);
    }

    @Test
    void deleteTaskRedirectsWithSuccessMessage() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = taskController.deleteTask(4L, redirectAttributes);

        assertEquals("redirect:/tasks", viewName);
        assertEquals("Task deleted successfully.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
        verify(taskService).deleteTask(4L);
    }

    private Task createValidTask() {
        Task task = new Task();
        task.setTitle("Test task");
        task.setDescription("Test description");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setStatus("Pending");
        return task;
    }
}
