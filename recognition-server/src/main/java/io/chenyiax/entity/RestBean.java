package io.chenyiax.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chenyiax.exception.JsonException;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * RestBean 类用于封装 RESTful API 的响应信息，是一个通用的响应实体类。
 * 借助泛型 <T> 可以支持不同类型的数据返回。
 *
 * @param <T> 响应中携带的具体数据类型
 */
@Data
@AllArgsConstructor
public class RestBean<T> {

    /**
     * 响应的状态码，例如 200 表示成功，404 表示资源未找到等。
     */
    private int code;

    /**
     * 响应的消息，用于简要描述本次请求的处理结果。
     */
    private String message;

    /**
     * 响应携带的具体数据，数据类型由泛型 <T> 决定。
     */
    private T data;

    /**
     * 快速创建一个表示请求成功的 RestBean 实例，不携带具体数据。
     * 默认状态码为 200，消息为 "Success"。
     *
     * @param <T> 响应数据的类型
     * @return 一个表示成功的 RestBean 实例
     */
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, "Success", null);
    }

    /**
     * 快速创建一个表示请求成功的 RestBean 实例，并携带具体数据。
     * 默认状态码为 200，消息为 "Success"。
     *
     * @param <T> 响应数据的类型
     * @param data 响应携带的具体数据
     * @return 一个表示成功且携带数据的 RestBean 实例
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, "Success", data);
    }

    /**
     * 快速创建一个表示请求失败的 RestBean 实例。
     * 可以自定义失败的状态码和提示消息，不携带具体数据。
     *
     * @param <T> 响应数据的类型
     * @param code 失败的状态码
     * @param message 失败的提示消息
     * @return 一个表示失败的 RestBean 实例
     */
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, message, null);
    }

    /**
     * 将当前 RestBean 实例转换为 JSON 字符串。
     * 使用 Jackson 的 ObjectMapper 进行对象到 JSON 的序列化操作。
     *
     * @return 表示当前 RestBean 实例的 JSON 字符串
     * @throws JsonException 若在序列化过程中发生 JSON 处理异常
     */
    public String asJsonString() {
        try {
            // 使用 ObjectMapper 将当前对象转换为 JSON 字符串
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // 捕获 JSON 处理异常并抛出自定义的 JsonException
            throw new JsonException("JSON serialization failed");
        }
    }
}