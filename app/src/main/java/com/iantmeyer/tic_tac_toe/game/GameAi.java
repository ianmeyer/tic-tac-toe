package com.iantmeyer.tic_tac_toe.game;

import android.os.AsyncTask;
import android.util.Log;

import com.iantmeyer.tic_tac_toe.util.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class will perform recursive minimax logic to determine the best available move for a game.
 * <br><br>
 * The game must implement GameAiInterface and have moves which implement MoveInterface
 */
public class GameAi {
    private static final String TAG = "GameAi";

    private static BestMoveAsyncTask asyncTask;
    private static int mMaxDepth = -1;

    private GameAi() {
        // discourage instantiation
    }

    /**
     * Set the maximum depth allowed by the minimax algorithm recursion.
     * <p/>
     * At this depth, getGameValue() will be used, rather than continue recursion.
     *
     * @param maxDepth
     */
    public static void with(int maxDepth) {
        mMaxDepth = maxDepth;
    }

    /**
     * The AI will calculate the best available next move for the game provided
     *
     * @param game
     * @return
     */
    public static boolean makeAiMove(GameAiInterface game) {
        if (asyncTask == null && game != null) {
            BusProvider.INSTANCE.getBus().post(new AiStartEvent(game.getNextPlayer()));
            new BestMoveAsyncTask().execute(game);
            return true;
        } else {
            Log.e(TAG, "The Game Ai is already calculating a move");
        }
        return false;
    }

    /**
     * Otto Event published when Game AI begins
     */
    public static class AiStartEvent {
        public final int player;

        AiStartEvent(int player) {
            this.player = player;
        }
    }

    /**
     * Otto Event published when Game AI finishes calculations
     */
    public static class AiFinishEvent {
        public final MoveInterface move;

        AiFinishEvent(MoveInterface move) {
            this.move = move;
        }
    }

    private static class BestMoveAsyncTask extends AsyncTask<GameAiInterface, Void, GameAi.MoveInterface> {
        @Override
        protected MoveInterface doInBackground(GameAiInterface... params) {
            GameAiInterface game = params[0];
            MoveInterface bestMove = getBestMove(game);
            return bestMove;
        }

        @Override
        protected void onPostExecute(MoveInterface move) {
            Log.i(TAG, "Found best move for player: " + move.getPlayer());
            BusProvider.INSTANCE.getBus().post(new AiFinishEvent(move));
            asyncTask = null;
        }
    }

    private static MoveInterface getBestMove(GameAiInterface game) {
        ArrayList<MoveInterface> availableMoves = game.getAvailableMoves();
        ArrayList<MoveOutcome> availableMoveOutcomes = new ArrayList<>();
        for (int idx = 0; idx < availableMoves.size(); idx++) {
            availableMoveOutcomes.add(getMoveOutcome(game, availableMoves.get(idx), 0));
        }
        int bestMoveIdx = getBestOutcomeIdx(availableMoveOutcomes, game.getNextPlayer());

        return availableMoves.get(bestMoveIdx);
    }

    private static MoveOutcome getMoveOutcome(GameAiInterface game, MoveInterface move, int depth) {
        if (game.isWinningMove(move)) {
            return new MoveOutcome(Endgame.WIN, move.getPlayer());
        } else if (game.isDrawMove(move)) {
            return new MoveOutcome(Endgame.DRAW, 0);
        } else {
            GameAiInterface nextGame = game.createGameAfterMove(move);
            ArrayList<MoveInterface> availableNextMoves = nextGame.getAvailableMoves();
            ArrayList<MoveOutcome> availableNextMoveOutcomes = new ArrayList<>();

            if (depth == mMaxDepth) {
                return new MoveOutcome(nextGame.getGameValue(move.getPlayer()));

            } else {
                for (int idx = 0; idx < availableNextMoves.size(); idx++) {
                    availableNextMoveOutcomes.add(getMoveOutcome(nextGame, availableNextMoves.get(idx), depth + 1));
                }
                return getBestOutcome(availableNextMoveOutcomes, nextGame.getNextPlayer());
            }
        }
    }

    private static MoveOutcome getBestOutcome(ArrayList<MoveOutcome> outcomes, int player) {
        int bestIdx = getBestOutcomeIdx(outcomes, player);
        return outcomes.get(bestIdx);
    }

    private static int getBestOutcomeIdx(ArrayList<MoveOutcome> outcomes, int player) {
        if (outcomes.size() == 1) {
            return 0;
        }
        ArrayList<Integer> wins = new ArrayList<>();
        ArrayList<Integer> draws = new ArrayList<>();
        ArrayList<Integer> losses = new ArrayList<>();
        ArrayList<Integer> highValues = new ArrayList<>();
        double highestValue = 0;

        ArrayList<Integer> bestOutcomes;

        for (int idx = 0; idx < outcomes.size(); idx++) {
            if (outcomes.get(idx).endgame == Endgame.WIN) {
                if (outcomes.get(idx).player == player) {
                    wins.add(idx);
                } else {
                    losses.add(idx);
                }
            } else if (outcomes.get(idx).endgame == Endgame.DRAW) {
                draws.add(idx);
            } else if (outcomes.get(idx).endgame == Endgame.UNKNOWN) {
                if (outcomes.get(idx).value > highestValue || highValues.isEmpty()) {
                    highValues.clear();
                    highestValue = outcomes.get(idx).value;
                }
                highValues.add(idx);
            }
        }
        if (!wins.isEmpty()) {
            bestOutcomes = wins;
        } else if (!highValues.isEmpty()) {
            bestOutcomes = highValues;
        } else if (!draws.isEmpty()) {
            bestOutcomes = draws;
        } else {
            bestOutcomes = losses;
        }

        if (bestOutcomes.size() == 1) {
            return bestOutcomes.get(0);
        } else {
            // Randomly select among equally valued moves, to keep things feeling fresh
            Random randomGen = new Random();
            int randomIdx = randomGen.nextInt(bestOutcomes.size());
            return bestOutcomes.get(randomIdx);
        }
    }

    /**
     * A game which can be played by GameAi must implement the GameAiInterface
     */
    public interface GameAiInterface {
        /**
         * the next player to move in the game
         *
         * @return integer
         */
        int getNextPlayer();

        /**
         * Whether or not a specific move results in a win
         *
         * @param move
         * @return true for win
         */
        boolean isWinningMove(MoveInterface move);

        /**
         * Whether or not a specific move results in a draw
         *
         * @param move
         * @return true for draw
         */
        boolean isDrawMove(MoveInterface move);

        /**
         * Create a copy of a game in which a certain move is made.
         * The games created by this function are used to evaluate
         * the outcomes of different possible moves.
         *
         * @param move
         * @return
         */
        GameAiInterface createGameAfterMove(MoveInterface move);

        /**
         * An array of available moves in the current game
         *
         * @return
         */
        ArrayList<MoveInterface> getAvailableMoves();

        /**
         * A heuristic evaulation of the board state for a given player
         * <br><br>
         * Higher positive numbers represent a better predicted outcome for the player in question
         *
         * @return
         */
        double getGameValue(int player);

        /**
         * TODO IM
         * @param aiFinishEvent
         */
        @Subscribe
        void onAiFinishEvent(AiFinishEvent aiFinishEvent);
    }

    /**
     * A game which can be played by the GameAi needs to have
     * Move objects which implements the MoveInterface
     */
    public interface MoveInterface {
        /**
         * The player who performs this move
         *
         * @return
         */
        int getPlayer();
    }

    private enum Endgame {
        WIN,
        DRAW,
        UNKNOWN
    }

    private static class MoveOutcome {
        private Endgame endgame = Endgame.UNKNOWN;
        private int player;
        private double value;

        MoveOutcome(Endgame endgame, int player) {
            this.endgame = endgame;
            this.player = player;
        }

        MoveOutcome(double value) {
            this.endgame = Endgame.UNKNOWN;
            this.value = value;
        }
    }
}
