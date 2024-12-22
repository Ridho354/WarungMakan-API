package com.enigma.wmb_api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
// buat ValidationUtil #SPRING VALIDATION#
public class ValidationUtil {
    private final Validator validator;

    public void validate(Object object){
        Set<ConstraintViolation<Object>> constraintViolationErrors = validator.validate(object);
        if(!constraintViolationErrors.isEmpty()){
            throw new ConstraintViolationException(constraintViolationErrors);
        }
    }
}
