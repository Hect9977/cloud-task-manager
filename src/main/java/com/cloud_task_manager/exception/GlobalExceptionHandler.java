package com.cloud_task_manager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFound(
            TaskNotFoundException exception,
            HttpServletRequest request,
            Model model) {

        logger.warn("Task request failed: path={}, message={}",
                request.getRequestURI(), exception.getMessage());

        model.addAttribute("errorTitle", "Task Not Found");
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpectedError(
            Exception exception,
            HttpServletRequest request,
            Model model) {

        logger.error("Unexpected request failure: path={}",
                request.getRequestURI(), exception);

        model.addAttribute("errorTitle", "Application Error");
        model.addAttribute(
                "errorMessage",
                "The request could not be completed. Please return to the task list and try again.");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "error";
    }
}
