# MCP Server for Lichess.org

A Spring Boot application that serves as a Model Completion Provider (MCP) server for Lichess.org, allowing AI models to interact with the Lichess API.

## Description

This application provides a server that exposes Lichess API functionality as tools that can be used by AI models through the Spring AI framework. It allows AI models to:

- Fetch recent games from Lichess.org for a specific user
- Get a random game for a user within a specified time period
- Visualize chess positions using FEN (Forsyth–Edwards Notation)


## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mcp-lichess.git
   cd mcp-lichess
   ```

2. Build the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Configuration

The application can be configured using the following properties:

- `server.port`: The port on which the server will run (default: 8081)
- `lichess.api.token`: Your Lichess API token (optional)

You can set these properties in the `application.properties` file or as environment variables.

### Setting up Lichess API Token

While the application can work without a Lichess API token, it's recommended to use one to avoid rate limiting. You can obtain a token from your Lichess account settings.

Set the token as an environment variable:
```bash
export LICHESS_API_TOKEN=your_token_here
```

Or add it to your `application.properties` file:
```properties
lichess.api.token=your_token_here
```

## API Tools

The application exposes the following tools through the Spring AI MCP server:

### Last Games

Fetches the last N games from Lichess.org for a specific user.

Parameters:
- `username`: The Lichess username to fetch games for
- `n`: Number of games to fetch

### Random Game

Fetches a random game from Lichess.org for a specific user within a specified time period.

Parameters:
- `username`: The Lichess username to fetch a game for
- `days`: How many days back to look for games

### Board from FEN

Visualizes a chess position from a FEN string.

Parameters:
- `fen`: FEN notation of the chess position to display

## Usage with Spring AI

This MCP server can be used with Spring AI to enable AI models to interact with Lichess.org. The server exposes tools that can be called by AI models to fetch and analyze chess games.

Example usage with Spring AI client:
```java
// Configure the MCP client to connect to this server
McpClient mcpClient = McpClient.builder()
    .baseUrl("http://localhost:8081")
    .build();

// Use the client to call the Lichess API tools
McpResponse response = mcpClient.call("Tell me about the last 5 games of user 'DrNykterstein'");
```