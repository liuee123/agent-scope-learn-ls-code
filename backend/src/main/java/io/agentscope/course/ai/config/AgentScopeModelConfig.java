package io.agentscope.course.ai.config;

import io.agentscope.core.model.Model;
import io.agentscope.core.model.ModelRegistry;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;



/**
 * AgentScope 模型配置。
 *
 * <p>在 Spring 容器启动后，将 DeepSeek 和 DashScope 两个模型注册到
 * {@link ModelRegistry}，后续 Agent 通过模型名称字符串即可引用。</p>
 *
 * <p>API Key 从环境变量中读取，不在代码中硬编码：</p>
 * <ul>
 *   <li>{@code DEEPSEEK_API_KEY} — DeepSeek 平台 API Key</li>
 *   <li>{@code DASHSCOPE_API_KEY} — 阿里云百炼平台 API Key</li>
 * </ul>
 */
@Configuration
public class AgentScopeModelConfig {

    private static final Logger log = LoggerFactory.getLogger(AgentScopeModelConfig.class);

    public static final String MODEL_DEEPSEEK = "deepseek";
    public static final String MODEL_DASHSCOPE = "dashscope";

    @PostConstruct
    public void registerModels() {
        // ── 1. DeepSeek 模型（OpenAI 兼容协议） ─────────────────────
        String deepseekApiKey = System.getenv("DEEPSEEK_API_KEY");
        if (deepseekApiKey != null && !deepseekApiKey.isBlank()) {
            Model deepseek = OpenAIChatModel.builder()
                    .apiKey(deepseekApiKey)
                    .modelName("deepseek-v4-flash")
                    .baseUrl("https://api.deepseek.com")
                    .stream(true)
                    .build();
            ModelRegistry.register(MODEL_DEEPSEEK, deepseek);
            log.info("Registered model: deepseek (deepseek-v4-flash)");
        } else {
            log.warn("DEEPSEEK_API_KEY not set — model 'deepseek' unavailable");
        }

        // ── 2. DashScope 模型（阿里云百炼） ─────────────────────────
        String dashscopeApiKey = System.getenv("DASHSCOPE_API_KEY");
        if (dashscopeApiKey != null && !dashscopeApiKey.isBlank()) {
            Model dashscope = DashScopeChatModel.builder()
                    .apiKey(dashscopeApiKey)
                    .modelName("deepseek-v4-flash")
                    .baseUrl("https://llm-axroyp679b6wbyo7.cn-beijing.maas.aliyuncs.com/api/v1")
                    .stream(true)
                    .build();
            ModelRegistry.register(MODEL_DASHSCOPE, dashscope);
            log.info("Registered model: dashscope (qwen-plus)");
        } else {
            log.warn("DASHSCOPE_API_KEY not set — model 'dashscope' unavailable");
        }
    }

    /**
     * 检查给定的模型名是否为已注册的合法模型。
     *
     * @param modelName 模型名称
     * @return true 如果模型名合法
     */
    public static boolean isValidModel(String modelName) {
        return MODEL_DEEPSEEK.equals(modelName) || MODEL_DASHSCOPE.equals(modelName);
    }
}
