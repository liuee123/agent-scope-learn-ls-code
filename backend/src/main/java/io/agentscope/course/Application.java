package io.agentscope.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AgentScope 课程 — Spring Boot 启动入口。
 *
 * <p>作为纯入口类，不包含任何业务逻辑。各章节的功能代码
 * 位于 {@code config/}、{@code service/}、{@code controller/} 子包中。</p>
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
