package com.cloud_task_manager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // Handle requests for a task that does not exist.
        @ExceptionHandler(TaskNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public String handleTaskNotFound(
                        TaskNotFoundException exception,
                        HttpServletRequest request,
                        Model model) {

                logger.warn(
                                "event=TASK_NOT_FOUND method=handleTaskNotFound "
                                                + "path={} message={}",
                                request.getRequestURI(),
                                exception.getMessage());

                model.addAttribute("errorTitle", "Task Not Found");
                model.addAttribute("errorMessage", exception.getMessage());
                model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());

                return "error";
        }

        // Handle missing pages and static resources, including favicon.ico.
        @ExceptionHandler(NoResourceFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public String handleResourceNotFound(
                        NoResourceFoundException exception,
                        HttpServletRequest request,
                        Model model) {

                logger.debug(
                                "event=RESOURCE_NOT_FOUND method=handleResourceNotFound "
                                                + "path={} status=404",
                                request.getRequestURI());

                model.addAttribute("errorTitle", "Page Not Found");
                model.addAttribute(
                                "errorMessage",
                                "The requested page or resource could not be found.");
                model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());

                return "error";
        }

        // Log unexpected failures with their stack traces.
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public String handleUnexpectedError(
                        Exception exception,
                        HttpServletRequest request,
                        Model model) {

                logger.error(
                                "event=UNEXPECTED_ERROR method=handleUnexpectedError "
                                                + "path={} message=Request failed",
                                request.getRequestURI(),
                                exception);

                model.addAttribute("errorTitle", "Application Error");
                model.addAttribute(
                                "errorMessage",
                                "The request could not be completed. "
                                                + "Please return to the task list and try again.");
                model.addAttribute(
                                "statusCode",
                                HttpStatus.INTERNAL_SERVER_ERROR.value());

                return "error";
        }
}