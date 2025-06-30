package org.shelajev.mcpchess;

import chariot.util.Board;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.jbang.jash.Jash.$;

@Service
public class Stockfish {


    @Tool(description = "You have the stockfish binary on the PATH, run a stockfish command to analyze a position or a game.")
    String findBestMove(@ToolParam(description = "FEN of the position to analyse") String fen) {
        String stockfishOutput = runStockfish(fen);
        return stockfishOutput;
    }

    private static String runStockfish(String fen) {
        int movetimeInMilliseconds = 1200;
        String command = """
                expect -c "spawn stockfish; send \\"uci\\n\\"; expect \\"uciok\\" ; send \\"setoption name MultiPV value 1\\n\\"; send \\"position fen %s\\n\\"; send \\"go movetime %d\\n\\"; sleep %f; send \\"quit\\n\\"; interact" 
                """.formatted(fen, movetimeInMilliseconds, 1.5);

        String stockfishOutput = $(command).get();
        return stockfishOutput;
    }

    record Eval(String fen, String eval) {
    }

    @Tool(description = "Analyze a sequence of chess moves and return evaluations for each position.")
    List<Eval> analyzeGame(@ToolParam(description = "List of chess moves in SAN notation") List<String> moves) {
        // Configuration parameters
        List<Eval> evaluations = new ArrayList<>();

        // Validate input
        if (moves == null || moves.isEmpty()) {
            return null;
        }

        Board board = Board.fromStandardPosition();
        // Analyze each position after applying moves
        for (int i = 0; i < moves.size(); i++) {
            board = board.play(moves.get(i));
            String fen = board.toFEN();

            String output = runStockfish(fen);

            String eval = extractEvaluation(output);
            var evaluation = new Eval(fen, eval);
            evaluations.add(evaluation);
        }
        return evaluations;

    }

    /**
     * Extracts the evaluation score from Stockfish output.
     *
     * @param stockfishOutput The raw output from Stockfish
     * @return A string representation of the evaluation (e.g., "0.45" or "mate in 3")
     */
    private String extractEvaluation(String stockfishOutput) {
        Pattern pattern = Pattern.compile("info depth (\\d+) .*score (cp|mate) (-?\\d+)");
        Matcher matcher = pattern.matcher(stockfishOutput);

        // Find the last evaluation (most accurate)
        String lastEval = "unknown";
        int highestDepth = 0;

        while (matcher.find()) {
            int depth = Integer.parseInt(matcher.group(1));
            String type = matcher.group(2);
            String value = matcher.group(3);

            // Only use evaluations from higher depths
            if (depth >= highestDepth) {
                highestDepth = depth;

                if ("cp".equals(type)) {
                    lastEval = value;
                } else if ("mate".equals(type)) {
                    lastEval = "mate in " + value;
                }
            }
        }

        return lastEval;
    }
}
