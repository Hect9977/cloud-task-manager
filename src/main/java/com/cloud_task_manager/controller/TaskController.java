package com.cloud_task_manager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloud_task_manager.model.Task;
import com.cloud_task_manager.services.TaskService;

import jakarta.validation.Valid;

@Controller
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String showHomePage() {
        logger.debug("Displaying the home page");
        return "index";
    }

    @GetMapping("/tasks")
    public String showTaskList(Model model) {
        logger.debug("Displaying the task list");
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task-list";
    }

    @GetMapping("/tasks/new")
    public String showCreateForm(Model model) {
        logger.debug("Displaying the create-task form");
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @PostMapping("/tasks/save")
    public String saveTask(
            @Valid @ModelAttribute("task") Task task,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("Task form validation failed with {} error(s)", result.getErrorCount());
            return "task-form";
        }

        boolean isNewTask = task.getId() == null;
        Task savedTask = taskService.saveTask(task);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                isNewTask ? "Task created successfully." : "Task updated successfully.");

        logger.info("Task form processed successfully: id={}, operation={}",
                savedTask.getId(), isNewTask ? "create" : "update");
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Displaying edit form for task ID {}", id);
        model.addAttribute("task", taskService.getTaskById(id));
        return "task-form";
    }

    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully.");
        logger.info("Delete request completed for task ID {}", id);
        return "redirect:/tasks";
    }
}
