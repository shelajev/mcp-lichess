package org.shelajev.mcp_lichess;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpLichessApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpLichessApplication.class, args);
	}
	@Bean
	ToolCallbackProvider serviceToolCallbackProvider(LichessAPIClient lichessAPIClient) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(lichessAPIClient)
				.build();
	}
}
