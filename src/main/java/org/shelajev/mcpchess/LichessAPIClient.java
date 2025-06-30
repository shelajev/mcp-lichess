package org.shelajev.mcpchess;


import chariot.Client;
import chariot.model.Game;
import chariot.model.Many;
import chariot.util.Board;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LichessAPIClient {

    @Value("$lichess.api.token")
    private String API_TOKEN;

    private Client client = Client.basic();

    @PostConstruct
    private void init() {
        if (API_TOKEN != null && API_TOKEN.isEmpty()) {
            client = client.withToken(API_TOKEN);
        }
    }

    @Tool(description = "fetch the last games from lichess.org by a given username")
    public List<Game> lastGames(
            @ToolParam(description = "the username to fetch the games") String username,
            @ToolParam(description = "how many games to fetch") int n) {

        Many<Game> games = client.games().byUserId(username.trim().toLowerCase(), searchFilter -> {
            searchFilter
                    .max(n)
                    .rated()
                    .finished()
                    .lastFen(true);
        });
        var returnMe = games.stream().toList();
        return returnMe;
    }

    @Tool(description = "fetch a random game from lichess.org by a given username")
    public Game randomGame(
            @ToolParam(description = "the username to fetch the games") String username,
            @ToolParam(description = "how many days back to look") int days) {
        Many<Game> games = client.games().byUserId(username.trim().toLowerCase(), searchFilter -> {
            searchFilter
                    .max(100)
                    .since(Instant.now().minus(Duration.ofDays(days)).toEpochMilli())
                    .rated()
                    .lastFen(true)
                    .finished();
        });
        var returnMe = games.stream().collect(toList());
        Collections.shuffle(returnMe);
        if(returnMe.isEmpty()) {
            return null;
        }
        return returnMe.getFirst();
    }

    @Tool (description = "returns a string which is a visualization of a chess board representation of the chess position given as FEN")
    public String boardFromFen(@ToolParam(description = "FEN of the position to display") String fen) {
        Board board = Board.fromFEN(fen);
        return board.toString();
    }


}
