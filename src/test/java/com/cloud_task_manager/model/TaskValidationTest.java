package com.cloud_task_manager.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class TaskValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void validTaskHasNoValidationErrors() {
        Task task = createValidTask();

        assertTrue(validator.validate(task).isEmpty());
    }

    @Test
    void blankTitleFailsValidation() {
        Task task = createValidTask();
        task.setTitle("   ");

        assertTrue(invalidProperties(task).contains("title"));
    }

    @Test
    void longDescriptionFailsValidation() {
        Task task = createValidTask();
        task.setDescription("x".repeat(501));

        assertTrue(invalidProperties(task).contains("description"));
    }

    @Test
    void missingDueDateFailsValidation() {
        Task task = createValidTask();
        task.setDueDate(null);

        assertTrue(invalidProperties(task).contains("dueDate"));
    }

    @Test
    void unsupportedStatusFailsValidation() {
        Task task = createValidTask();
        task.setStatus("Unknown");

        assertTrue(invalidProperties(task).contains("status"));
    }

    private Set<String> invalidProperties(Task task) {
        return validator.validate(task).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    private Task createValidTask() {
        Task task = new Task();
        task.setTitle("Complete Activity 2");
        task.setDescription("Finish validation and testing.");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setStatus("In Progress");
        return task;
    }
}
