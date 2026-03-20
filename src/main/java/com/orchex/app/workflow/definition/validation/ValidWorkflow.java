package com.orchex.app.workflow.definition.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WorkflowCycleValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkflow {

    String message() default "Invalid workflow definition";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
