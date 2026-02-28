package zp.gde.hu.alkfetmcpsvc.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zp.gde.hu.alkfetmcpsvc.tool.RootCertificateTool;
import zp.gde.hu.alkfetmcpsvc.tool.UserCertificateTool;

@Configuration
public class McpToolConfig {

    @Bean
    public ToolCallbackProvider certificateTools(
            RootCertificateTool rootCertificateTool,
            UserCertificateTool userCertificateTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(rootCertificateTool, userCertificateTool)
                .build();
    }
}

