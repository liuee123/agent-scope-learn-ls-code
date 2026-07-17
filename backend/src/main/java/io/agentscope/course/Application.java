package io.agentscope.course;

import io.agentscope.core.Version;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 第 1 节：课程导览与环境搭建 —— 项目启动入口。
 *
 * <p>启动后打印 AgentScope 版本、JDK 版本、操作系统信息，验证环境搭建是否成功。</p>
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("  AgentScope 课程项目启动成功！");
        System.out.println("========================================");
        System.out.println("  AgentScope 版本 : " + Version.VERSION);
        System.out.println("  JDK 版本        : " + System.getProperty("java.version"));
        System.out.println("  操作系统         : " + System.getProperty("os.name"));
        System.out.println("  User-Agent      : " + Version.getUserAgent());
        System.out.println("========================================");
    }
}
