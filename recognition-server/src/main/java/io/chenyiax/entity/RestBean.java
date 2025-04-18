package io.chenyiax.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestBean<T> {
    private int code;    // 状态码（例如 200=成功，401=未授权）
    private String message; // 提示信息
    private T data;      // 实际业务数据

    // 快速创建成功响应（无数据）
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, "操作成功", null);
    }

    // 快速创建成功响应（带数据）
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, "操作成功", data);
    }

    // 快速创建失败响应
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, message, null);
    }

    // 将对象转为 JSON 字符串（需要 JSON 库）
    public String asJsonString() {
        // 使用 Jackson 或 Gson 进行序列化
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}