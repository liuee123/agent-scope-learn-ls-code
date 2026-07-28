package io.agentscope.course.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理——将业务异常转换为标准 HTTP 错误响应。
 *
 * <p>所有 handler 限定 {@code produces = APPLICATION_JSON}，
 * 避免 SSE/EventStream 客户端因 Accept 头不匹配触发二次异常。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 —— 请求路径不存在（含静态资源回退）。 */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return errorBody(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    /** 400 —— 业务参数非法。 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return errorBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    /** 500 —— 兜底异常。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Internal server error", ex);
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    private static ResponseEntity<Map<String, Object>> errorBody(
            HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
