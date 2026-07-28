package io.agentscope.course.ai.dto;

/**
 * 聊天响应 DTO。
 *
 * @param reply     模型回复文本
 * @param sessionId 本次对话的会话 ID，可用于后续续接
 */
public record ChatResponse(String reply, String sessionId) {
}
