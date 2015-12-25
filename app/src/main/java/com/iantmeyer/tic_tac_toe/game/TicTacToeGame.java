package com.iantmeyer.tic_tac_toe.game;

import android.util.Log;

import com.iantmeyer.tic_tac_toe.util.BusProvider;

import java.util.ArrayList;
import java.util.Arrays;

import com.squareup.otto.Subscribe;

/**
 * A game of Tic-Tac-Toe implementing an algorithm for a computer player
 */
public class TicTacToeGame implements GameAi.GameAiInterface {

    private static final String TAG = "TicTacToeGame";

    private int mBoard[][];
    private int mBoardSize = 3;
    private int mNextPlayer = 1;

    private State mState = State.SET_UP;

    private boolean[] mPlayerHuman = new boolean[2];

    /**
     * The state of the tic-tac-toe game
     */
    public enum State {
        PLAYER_1_WON,
        PLAYER_2_WON,
        DRAW,
        SET_UP,
        IN_PROGRESS
    }

    /**
     * Game of Tic-tac game
     */
    public TicTacToeGame(int[][] board) {
        Log.i(TAG, "Creating game");
        setBoard(board);
        BusProvider.INSTANCE.getBus().register(this);
    }

    private TicTacToeGame(int[][] board, Move move) {
        setBoard(board);
        if (move != null) {
            mBoard[move.row][move.column] = move.getPlayer();
            mNextPlayer = getPlayerAfter(move.getPlayer());
        }
    }

    /**
     * Clear the board and reset the game.
     * <br><br>
     * If the computer is set to move first, then a GameAi move will be initiated
     *
     * @param firstPlayer the player who will have the first turn
     */
    public void resetGame(int firstPlayer) {
        Log.i(TAG, "Game reset");
        mState = State.SET_UP;
        int size = mBoard.length;
        mBoard = new int[size][size];
        mState = State.IN_PROGRESS;
        if (0 < firstPlayer && firstPlayer <= 2) {
            Log.i(TAG, "Setting first player as " + firstPlayer);
            mNextPlayer = firstPlayer;
        } else {
            Log.e(TAG, "Tic-tac-toe has two players. The player number must be either 1 or two: " + firstPlayer + " is invalid");
        }

        BusProvider.INSTANCE.getBus().post(new ResetGameEvent());

        if (!isPlayerHuman(mNextPlayer)) {
            Log.i(TAG, "The first player is a computer player and will make the first move.");
            GameAi.makeAiMove(this);
        }
    }

    /**
     * Otto event triggered when the Tic-Tac-Toe game is reset
     */
    public static class ResetGameEvent {
    }

    /**
     * The size of the game board
     *
     * @return
     */
    public int getBoardSize() {
        return mBoard.length;
    }

    /**
     * The State of the game
     *
     * @return
     */
    public State getState() {
        return mState;
    }

    /**
     * TODO IM
     *
     * @param row
     * @param column
     * @return
     */
    public int getPlayerAtPosition(int row, int column) {
        if (row < 0 || row > mBoard.length || column < 0 || column > mBoard[0].length) {
            return 0;
        }
        return mBoard[row][column];
    }

    /**
     * Perform a move in the game.
     * <br>
     * If the following player is a computer, their move will be triggered.
     *
     * @param move
     * @return
     */
    public boolean makeMove(Move move) {
        if (mState != State.SET_UP && mState != State.IN_PROGRESS) {
            return false;
        }
        if (mBoard[move.row][move.column] != 0) {
            return false;
        }
        if (mNextPlayer != 0 && mNextPlayer != move.getPlayer()) {
            return false;
        }
        Log.i(TAG, "Making move ( " + move.row + ", " + move.column + " ) for player " + move.getPlayer());
        if (isWinningMove(move)) {
            if (move.getPlayer() == 1) {
                mState = State.PLAYER_1_WON;
                Log.i(TAG, "Move creates a win for player: 1");
            } else {
                mState = State.PLAYER_2_WON;
                Log.i(TAG, "Move creates a win for player: 2");
            }
        } else if (isDrawMove(move)) {
            Log.i(TAG, "Move creates a draw!");
            mState = State.DRAW;
        }
        if (mState == State.SET_UP) {
            mState = State.IN_PROGRESS;
        }

        mBoard[move.row][move.column] = move.getPlayer();
        mNextPlayer = getPlayerAfter(move.getPlayer());

        BusProvider.INSTANCE.getBus().post(new GameMoveEvent(move));

        if (mState.equals(State.IN_PROGRESS) && !isPlayerHuman(mNextPlayer)) {
            GameAi.makeAiMove(this);
        }

        return true;
    }

    /**
     * Set whether or not a player is human
     *
     * @param player
     * @param human
     */
    public void setPlayerHuman(int player, boolean human) {
        if (0 <= player && player <= 2) {
            Log.i(TAG, "Setting player " + player + " human = " + human);
            mPlayerHuman[player - 1] = human;
        }
        if(!human && player == mNextPlayer) {
            GameAi.makeAiMove(this);
        }
    }

    /**
     * Whether or not a specific player is human
     *
     * @param player
     */
    public boolean isPlayerHuman(int player) {
        if (0 < player && player <= 2) {
            return mPlayerHuman[player - 1];
        } else {
            Log.e(TAG, "Tic-tac-toe can only have two players!");
        }
        return true;
    }

    /**
     * The board state of the game as an array
     * <br>
     * 0 => empty square
     * <br>
     * 1, 2 => the player controlling a square
     */
    public int[][] getBoard() {
        return mBoard;
    }

    /**
     * The player who has the next move
     *
     * @return 1 or 2
     */
    @Override
    public int getNextPlayer() {
        return mNextPlayer;
    }

    @Override
    public GameAi.GameAiInterface createGameAfterMove(GameAi.MoveInterface move) {
        return new TicTacToeGame(mBoard, (Move) move);
    }

    @Override
    public ArrayList<GameAi.MoveInterface> getAvailableMoves() {
        ArrayList<GameAi.MoveInterface> availableMoves = new ArrayList<>();
        for (int row = 0; row < mBoard.length; row++) {
            for (int col = 0; col < mBoard[0].length; col++) {
                if (mBoard[row][col] == 0) {
                    availableMoves.add(new Move(mNextPlayer, row, col));
                }
            }
        }
        return availableMoves;
    }

    @Override
    public double getGameValue(int player) {
        Log.e(TAG, "Heuristic game evaluation has not been implemented!");
        return 0;
    }

    @Override
    public boolean isWinningMove(GameAi.MoveInterface testMove) {
        if (!isValidMove(testMove)) {
            return false;
        }

        Move move = (Move) testMove;
        int player = move.getPlayer();

        // Check same row
        boolean wonGame = true;
        for (int colIdx = 0; colIdx < mBoard[0].length; colIdx++) {
            if ((colIdx != move.column && player != mBoard[move.row][colIdx])) {
                wonGame = false;
                break;
            }
        }
        if (wonGame) {
            return true; // player controls entire row
        }

        // Check same column
        wonGame = true;
        for (int rowIdx = 0; rowIdx < mBoard.length; rowIdx++) {
            if ((rowIdx != move.row && player != mBoard[rowIdx][move.column])) {
                wonGame = false;
                break;   // player does not control entire row
            }
        }
        if (wonGame) {
            return true; // player controls entire column
        }

        // Check diagonal for column = row
        wonGame = true;
        if (move.row == move.column) {    // check if move is on diagonal
            for (int rowIdx = 0; rowIdx < mBoard.length; rowIdx++) {
                if ((rowIdx != move.row && player != mBoard[rowIdx][rowIdx])) {
                    wonGame = false;
                    break;   // player does not control entire diagonal
                }
            }
            if (wonGame) {
                return true; // player controls entire diagonal
            }
        }
        // Check diagonal for col = size - row
        wonGame = true;
        if (move.row == mBoard[0].length - move.column - 1) { // check if move is on diagonal
            for (int rowIdx = 0; rowIdx < mBoard.length; rowIdx++) {
                if ((rowIdx != move.row && player != mBoard[rowIdx][mBoard.length - rowIdx - 1])) {
                    wonGame = false;
                    break;   // player does not control entire reverse diagonal
                }
            }
            if (wonGame) {
                return true; // player controls entire reverse diagonal
            }
        }
        return false;
    }

    @Override
    public boolean isDrawMove(GameAi.MoveInterface testMove) {
        if (!isValidMove(testMove)) {
            return false;
        }

        Move move = (Move) testMove;

        int[][] board = new int[mBoard.length][mBoard[0].length];
        for (int idx = 0; idx < mBoard.length; idx++) {
            board[idx] = Arrays.copyOf(mBoard[idx], mBoard[idx].length);
        }
        board[move.row][move.column] = move.getPlayer();

        boolean winPossible;
        int foundPlayer;

        // Check same row
        for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            winPossible = true;
            foundPlayer = 0;
            for (int colIdx = 0; colIdx < board.length; colIdx++) {
                if (board[rowIdx][colIdx] != 0) {
                    if (foundPlayer == 0) {
                        foundPlayer = board[rowIdx][colIdx];
                    } else if (foundPlayer != board[rowIdx][colIdx]) {
                        winPossible = false;
                        break;
                    }
                }
            }
            if (winPossible) {
                return false;
            }
        }

        // Check same column
        for (int colIdx = 0; colIdx < board[0].length; colIdx++) {
            winPossible = true;
            foundPlayer = 0;
            for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
                if (board[rowIdx][move.column] != 0) {
                    if (foundPlayer == 0) {
                        foundPlayer = board[rowIdx][move.column];
                    } else if (foundPlayer != board[rowIdx][move.column]) {
                        winPossible = false;
                        break;
                    }
                }
            }
            if (winPossible) {
                return false;
            }
        }

        // Check diagonal for column = row
        winPossible = true;
        foundPlayer = 0;
        for (int idx = 0; idx < board.length; idx++) {
            if (foundPlayer == 0) {
                foundPlayer = board[idx][idx];
            } else if (board[idx][idx] != 0 && foundPlayer != board[idx][idx]) {
                winPossible = false;
                break;
            }
        }
        if (winPossible) {
            return false;
        }

        // Check diagonal for col = size - row
        winPossible = true;
        foundPlayer = 0;
        for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            if (foundPlayer == 0) {
                foundPlayer = board[rowIdx][board.length - rowIdx - 1];
            } else if (board[rowIdx][rowIdx] != 0 && foundPlayer != board[rowIdx][board.length - rowIdx - 1]) {
                winPossible = false;
                break;
            }
        }
        if (winPossible) {
            return false;
        }
        return true;
    }

    private boolean validBoard(int[][] board) {
        if (board == null || board.length != board[0].length) {
            return false;
        }
        for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            for (int colIdx = 0; colIdx < board[0].length; colIdx++) {
                int value = board[rowIdx][colIdx];
                if (value < 0 || value > 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setBoard(int[][] board) {
        if (validBoard(board)) {
            mBoard = new int[board.length][board[0].length];
            for (int idx = 0; idx < board.length; idx++) {
                mBoard[idx] = Arrays.copyOf(board[idx], board[idx].length);
            }
        } else {
            Log.wtf(TAG, "The board must be square!");
            mBoard = new int[mBoardSize][mBoardSize];
        }
    }

    private int getPlayerAfter(int player) {
        return (player == 1 ? 2 : 1);
    }

    private boolean isValidMove(GameAi.MoveInterface testMove) {
        Move move = (Move) testMove;
        if ((move.row < 0) || (move.row > (mBoard.length - 1))
                || (move.column < 0) || (move.column > (mBoard[0].length - 1))
                || (mBoard[move.row][move.column] != 0)
                || (!mState.equals(State.IN_PROGRESS) && !mState.equals(State.SET_UP))) {
            return false;
        }
        return true;

    }

    public static class Move implements GameAi.MoveInterface {
        private final int mPlayer;
        public final int row;
        public final int column;

        /**
         * One move in a tic-tac-toe game
         *
         * @param player
         * @param row
         * @param column
         */
        public Move(int player, int row, int column) {
            mPlayer = player;
            this.row = row;
            this.column = column;
        }

        @Override
        public int getPlayer() {
            return mPlayer;
        }
    }

    /**
     * Otto event for a move having been made in the game
     */
    public static class GameMoveEvent {
        public final TicTacToeGame.Move move;

        public GameMoveEvent(Move move) {
            this.move = move;
        }
    }

    /**
     * When the
     *
     * @param aiFinishEvent
     */
    @Subscribe
    public void onAiFinishEvent(GameAi.AiFinishEvent aiFinishEvent) {
        if (aiFinishEvent.move instanceof TicTacToeGame.Move) {
            TicTacToeGame.Move move = (TicTacToeGame.Move) aiFinishEvent.move;
            if (move.getPlayer() == this.getNextPlayer()) {
                this.makeMove(move);
            } else {
                Log.e(TAG, "The GameAi is attempting to move the wrong player!");
            }
        }
    }

    public interface GameProvider {
        TicTacToeGame getGame();
    }
}
