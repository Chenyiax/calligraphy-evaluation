package io.chenyiax.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a custom business exception that extends the {@link RuntimeException}.
 * This exception is designed to handle business - related errors in the application.
 * It includes a custom error code and a descriptive error message.
 *
 * <p>The {@link Getter} annotation from Lombok is used to automatically generate getter methods
 * for the {@code code} and {@code message} fields.
 * The {@link AllArgsConstructor} annotation generates a constructor that takes all fields as parameters.</p>
 *
 * @author Your Name (Replace with actual author)
 */
@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    /**
     * A custom error code representing the type of business error.
     * This code can be used by the application to identify and handle different types of errors.
     */
    private int code;

    /**
     * A descriptive error message providing more details about the business error.
     * This message can be used for logging, debugging, or presenting to the user.
     */
    private String message;
}
