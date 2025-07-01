# MCP Server for Lichess.org

A Spring Boot application that serves as a Model Completion Provider (MCP) server for Lichess.org, allowing AI models to interact with the Lichess API and chess engines.

## Description

This application provides a server that exposes Lichess API functionality and chess engine analysis as tools that can be used by AI models through the Spring AI framework. It allows AI models to:

- Fetch recent games from Lichess.org for a specific user
- Get a random game for a user within a specified time period
- Visualize chess positions using FEN (Forsyth–Edwards Notation)
- Analyze chess positions using Stockfish
- Get human-like move suggestions using Maia

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/shelajev/mcp-lichess.git
   cd mcp-lichess
   ```

2. Build the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Docker build (recommended for chess engines support):
   ```bash
   docker build -t olegselajev241/mcp-chess:latest .
   docker run -p 8080:8080 -e LICHESS_API_TOKEN=your_token_here olegselajev241/mcp-chess:latest
   ```
   
4. You can of course run it without building the image first and pull the pre-built version: 
     ```bash
   docker run -p 8080:8080 -e LICHESS_API_TOKEN=your_token_here olegselajev241/mcp-chess:latest
   ```

## Configuration

The application can be configured using the following properties:

- `spring.application.name`: The name of the application (default: mcp-chess)
- `spring.ai.mcp.server.stdio`: Enable STDIO transport for MCP server (default: true)
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

### Lichess API Tools

#### Last Games

Fetches the last N games from Lichess.org for a specific user.

Parameters:
- `username`: The Lichess username to fetch games for
- `n`: Number of games to fetch

#### Random Game

Fetches a random game from Lichess.org for a specific user within a specified time period.

Parameters:
- `username`: The Lichess username to fetch a game for
- `days`: How many days back to look for games

#### Board from FEN

Visualizes a chess position from a FEN string.

Parameters:
- `fen`: FEN notation of the chess position to display

### Stockfish Engine Tools

#### Find Best Move

Analyzes a chess position using Stockfish and returns the raw output.

Parameters:
- `fen`: FEN notation of the chess position to analyze

#### Analyze Game

Analyzes a sequence of chess moves and returns evaluations for each position.

Parameters:
- `moves`: List of chess moves in SAN notation

### Maia Engine Tools

#### What Would Human Play

Uses the Maia chess engine to suggest a move that a human player of a specified rating might play.

Parameters:
- `fen`: FEN notation of the chess position to analyze
- `rating`: Rating of the Maia engine to use (from 1100 to 1900)

## Docker Support

The application includes a multi-stage Dockerfile that:
1. Builds the Spring Boot application
2. Builds the Stockfish chess engine
3. Builds the Leela Chess Zero (lc0) engine
4. Downloads Maia weights for ratings from 1100 to 1900
5. Creates a final image with all components

This ensures that all chess engines and required weights are available in the container.
