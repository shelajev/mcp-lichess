package org.shelajev.mcpchess;


import chariot.util.Board;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static dev.jbang.jash.Jash.$;

@Service
public class Maia {
    @Tool(description = "You have the Maia engine to analyze a chess position. Maia return 'human' moves rather than the best move. You can select at what strength level you want it to look for the move.")
    String whatWouldHumanPlay(@ToolParam(description = "FEN of the position to analyse") String fen,
                              @ToolParam(description = "Rating of the maia engine to use from 1100 till 1900") int rating) {

        int bucket = Math.max(1100, Math.min(1900, ((rating + 50) / 100) * 100));

        String command = """
                expect -c "spawn lc0 --weights=/maia/maia-%d.pb.gz; send \\"uci\\r\\"; expect \\"uciok\\"; send \\"position fen %s\\r\\"; send \\"go nodes 1\\r\\"; sleep 1; send \\"quit\\r\\"; interact"
                """.formatted(bucket, fen);
        String output = $(command).get();
        String result = output.lines().filter(line -> line.contains("bestmove")).collect(Collectors.joining("\n"));
        return result;
    }
}
