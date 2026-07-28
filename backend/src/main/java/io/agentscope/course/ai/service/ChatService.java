package io.agentscope.course.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.course.ai.dto.ChatRequest;
import io.agentscope.course.ai.dto.ChatResponse;
import io.agentscope.harness.agent.HarnessAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 聊天服务——注入 {@code AgentConfig} 中预构建的 HarnessAgent 实例。
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, HarnessAgent> agents;

    public ChatService(Map<String, HarnessAgent> agents) {
        this.agents = agents;
    }

    public ChatResponse chat(ChatRequest request) {
        validateRequest(request);
        String sessionId = resolveSessionId(request.sessionId());
        RuntimeContext ctx = buildRuntimeContext(sessionId);
        HarnessAgent agent = selectAgent(request.model());

        UserMessage userMsg = new UserMessage("default", request.message());
        Msg reply = agent.call(userMsg, ctx).block();

        String text = reply != null && reply.getTextContent() != null
                ? reply.getTextContent()
                : "";
        return new ChatResponse(text, sessionId);
    }

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
        validateRequest(request);
        String sessionId = resolveSessionId(request.sessionId());
        RuntimeContext ctx = buildRuntimeContext(sessionId);
        HarnessAgent agent = selectAgent(request.model());
        UserMessage userMsg = new UserMessage("default", request.message());

        Flux<String> tokenFlux = agent.streamEvents(userMsg, ctx)
                .filter(e -> e instanceof TextBlockDeltaEvent)
                .map(e -> ((TextBlockDeltaEvent) e).getDelta());

        return tokenFlux
                .map(delta -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("type", "token");
                    data.put("data", delta);
                    return sse("token", data);
                })
                .concatWithValues(doneFrame(sessionId))
                .onErrorResume(ex -> {
                    log.warn("Stream error: {}", ex.getMessage());
                    Map<String, Object> err = new LinkedHashMap<>();
                    err.put("type", "error");
                    err.put("error", ex.getMessage());
                    return Flux.just(sse("error", err));
                });
    }

    private void validateRequest(ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (request.model() == null || request.model().isBlank()) {
            throw new IllegalArgumentException("model must not be blank");
        }
        if (!agents.containsKey(request.model())) {
            throw new IllegalArgumentException(
                    "Unknown or unavailable model: '" + request.model()
                    + "'. Available: " + String.join(", ", agents.keySet()));
        }
    }

    private String resolveSessionId(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : UUID.randomUUID().toString();
    }

    private RuntimeContext buildRuntimeContext(String sessionId) {
        return RuntimeContext.builder()
                .userId("default")
                .sessionId(sessionId)
                .build();
    }

    private HarnessAgent selectAgent(String model) {
        return agents.get(model);
    }

    private ServerSentEvent<String> doneFrame(String sessionId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", "done");
        data.put("sessionId", sessionId);
        return sse("done", data);
    }

    private ServerSentEvent<String> sse(String eventType, Object data) {
        String json;
        try {
            json = MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            json = "{\"type\":\"" + eventType + "\"}";
        }
        return ServerSentEvent.<String>builder().event(eventType).data(json).build();
    }
}
