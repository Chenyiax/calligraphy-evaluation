package io.chenyiax.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chenyiax.exception.JsonException;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A generic class representing a unified RESTful response bean.
 * It encapsulates the response status code, message, and actual business data.
 *
 * @param <T> The type of the actual business data.
 */
@Data
@AllArgsConstructor
public class RestBean<T> {
    /**
     * The status code of the response.
     * Common values include 200 for success, 401 for unauthorized access, etc.
     */
    private int code;

    /**
     * The prompt message of the response, providing additional information about the result.
     */
    private String message;

    /**
     * The actual business data returned by the service.
     * The type is generic and can be any object.
     */
    private T data;

    /**
     * Quickly create a successful response without data.
     *
     * @param <T> The type of the actual business data.
     * @return A RestBean instance representing a successful response.
     */
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, "Success", null);
    }

    /**
     * Quickly create a successful response with data.
     *
     * @param <T>  The type of the actual business data.
     * @param data The actual business data to be included in the response.
     * @return A RestBean instance representing a successful response with data.
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, "Success", data);
    }

    /**
     * Quickly create a failed response.
     *
     * @param <T>     The type of the actual business data.
     * @param code    The status code of the failed response.
     * @param message The prompt message explaining the reason for the failure.
     * @return A RestBean instance representing a failed response.
     */
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, message, null);
    }

    /**
     * Convert the RestBean object to a JSON string.
     * This method uses Jackson's ObjectMapper to perform serialization.
     *
     * @return A JSON string representing the RestBean object.
     * @throws JsonException If a JSON processing exception occurs during serialization.
     */
    public String asJsonString() {
        // Use Jackson to serialize the object
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new JsonException("JSON serialization failed");
        }
    }
}