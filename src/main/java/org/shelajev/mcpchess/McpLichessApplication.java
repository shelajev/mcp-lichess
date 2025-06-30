package org.shelajev.mcpchess;

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
	ToolCallbackProvider serviceToolCallbackProvider(LichessAPIClient lichessAPIClient, Stockfish stockfish, Maia maia) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(lichessAPIClient, stockfish, maia)
				.build();
	}
}
