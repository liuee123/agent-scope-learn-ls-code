package io.agentscope.course.ai.dto;

/**
 * 聊天请求 DTO。
 *
 * @param message   用户消息（必填）
 * @param model     模型选择：{@code "deepseek"} 或 {@code "dashscope"}（必填）
 * @param sessionId 会话 ID（可选，不传则自动创建新会话）
 */
public record ChatRequest(String message, String model, String sessionId) {

    /** 支持的模型白名单。 */
    public static final String MODEL_DEEPSEEK = "deepseek";
    public static final String MODEL_DASHSCOPE = "dashscope";
}
