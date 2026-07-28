package io.agentscope.course.ai.controller;

import io.agentscope.course.ai.dto.ChatRequest;
import io.agentscope.course.ai.dto.ChatResponse;
import io.agentscope.course.ai.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 聊天 REST 接口。
 *
 * <ul>
 *   <li>{@code POST /api/v1/chat} — 同步对话，返回完整回复</li>
 *   <li>{@code POST /api/v1/chat/stream} — 流式对话，SSE 逐 token 推送</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 同步对话接口。
     *
     * <p>请求示例：</p>
     * <pre>{@code
     * POST /api/v1/chat
     * Content-Type: application/json
     *
     * {
     *   "message": "你好，请介绍一下你自己",
     *   "model": "deepseek",
     *   "sessionId": "optional-session-id"
     * }
     * }</pre>
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }

    /**
     * 流式对话接口（SSE）。
     *
     * <p>请求格式与同步接口相同。响应为 {@code text/event-stream} 流：</p>
     * <ul>
     *   <li>{@code event: token} — 增量文本块</li>
     *   <li>{@code event: done} — 对话结束</li>
     *   <li>{@code event: error} — 出错</li>
     * </ul>
     *
     * <p>客户端示例（curl）：</p>
     * <pre>{@code
     * curl -N -X POST http://localhost:8080/api/v1/chat/stream \
     *   -H "Content-Type: application/json" \
     *   -d '{"message":"你好","model":"deepseek"}'
     * }</pre>
     *
     * @param request 聊天请求
     * @return SSE 事件流
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(@RequestBody ChatRequest request) {
        return chatService.streamChat(request);
    }
}
