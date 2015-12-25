package com.iantmeyer.tic_tac_toe.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iantmeyer.tic_tac_toe.game.TicTacToeGame;
import com.iantmeyer.tic_tac_toe.game.TicTacToeGame.Move;

import static org.junit.Assert.*;

public class TicTacToeGameTest {

    private TicTacToeGame mGame;

    @Before
    public void setUp() throws Exception {
        mGame = new TicTacToeGame(null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetBoardSize() throws Exception {
        TicTacToeGame game = new TicTacToeGame(null);
        assertEquals(3, game.getBoardSize());
    }

    @Test
    public void testGetPlayer() throws Exception {
        mGame.resetGame(1);
        mGame.makeMove(new TicTacToeGame.Move(1, 1, 1));
        mGame.makeMove(new TicTacToeGame.Move(2, 2, 2));
        mGame.makeMove(new TicTacToeGame.Move(1, 0, 0));

        assertEquals(0, mGame.getPlayerAtPosition(0, 1));
        assertEquals(1, mGame.getPlayerAtPosition(0, 0));
        assertEquals(1, mGame.getPlayerAtPosition(1, 1));
        assertEquals(2, mGame.getPlayerAtPosition(2, 2));
    }

    @Test
    public void testMakeMove() throws Exception {
        mGame.resetGame(1);

        assertEquals(0, mGame.getPlayerAtPosition(1, 1));
        mGame.makeMove(new Move(1, 1, 1));
        assertEquals(1, mGame.getPlayerAtPosition(1, 1));

        assertEquals(0, mGame.getPlayerAtPosition(0, 0));
        mGame.makeMove(new Move(2, 0, 0));
        assertEquals(2, mGame.getPlayerAtPosition(0, 0));
    }

    @Test
    public void testGetNextPlayer() throws Exception {
        mGame.resetGame(1);

        assertEquals(1, mGame.getNextPlayer());

        mGame.makeMove(new Move(1, 0, 0));
        assertEquals(2, mGame.getNextPlayer());

        mGame.makeMove(new Move(2, 1, 0));
        assertEquals(1, mGame.getNextPlayer());
    }

    @Test
    public void testWinningMove() throws Exception {
        Move testMove;

        // Same row win
        mGame.resetGame(1);
        assertTrue(mGame.makeMove(new Move(1, 2, 0)));
        assertTrue(mGame.makeMove(new Move(2, 1, 0)));
        assertTrue(mGame.makeMove(new Move(1, 2, 1)));
        assertTrue(mGame.makeMove(new Move(2, 1, 1)));

        testMove = new TicTacToeGame.Move(1, 2, 2);
        assertTrue(mGame.isWinningMove(testMove));

        // Same column win
        mGame.resetGame(1);
        assertTrue(mGame.makeMove(new Move(1, 0, 2)));
        assertTrue(mGame.makeMove(new Move(2, 0, 0)));
        assertTrue(mGame.makeMove(new Move(1, 1, 2)));
        assertTrue(mGame.makeMove(new Move(2, 1, 0)));

        testMove = new TicTacToeGame.Move(1, 2, 2);
        assertTrue(mGame.isWinningMove(testMove));

        // Diagonal win
        mGame.resetGame(1);
        assertTrue(mGame.makeMove(new Move(1, 0, 0)));
        assertTrue(mGame.makeMove(new Move(2, 0, 1)));
        assertTrue(mGame.makeMove(new Move(1, 1, 1)));
        assertTrue(mGame.makeMove(new Move(2, 0, 2)));

        testMove = new TicTacToeGame.Move(1, 2, 2);
        assertTrue(mGame.isWinningMove(testMove));

        // Reverse diagonal win
        mGame.resetGame(1);
        assertTrue(mGame.makeMove(new Move(1, 2, 0)));
        assertTrue(mGame.makeMove(new Move(2, 0, 0)));
        assertTrue(mGame.makeMove(new Move(1, 1, 1)));
        assertTrue(mGame.makeMove(new Move(2, 1, 0)));

        testMove = new Move(1, 0, 2);
        assertTrue(mGame.isWinningMove(testMove));

        mGame.resetGame(1);
        assertTrue(mGame.makeMove(new Move(1, 2, 0)));
        assertTrue(mGame.makeMove(new Move(2, 2, 1)));
        assertTrue(mGame.makeMove(new Move(1, 1, 0)));
        assertTrue(mGame.makeMove(new Move(2, 0, 0)));
        assertTrue(mGame.makeMove(new Move(1, 0, 2)));
        assertTrue(mGame.makeMove(new Move(2, 1, 1)));

        testMove = new Move(1, 0, 1);
        assertFalse(mGame.isWinningMove(testMove));
        assertTrue(mGame.makeMove(testMove));

        testMove = new Move(2, 2, 2);
        assertTrue(mGame.isWinningMove(testMove));
    }

    @Test
    public void testDrawMove() throws Exception {
        Move testMove;

        mGame.resetGame(1);
        assertTrue(mGame.makeMove(new Move(1, 2, 0)));
        assertTrue(mGame.makeMove(new Move(2, 2, 1)));
        assertTrue(mGame.makeMove(new Move(1, 1, 0)));
        assertTrue(mGame.makeMove(new Move(2, 0, 0)));
        assertTrue(mGame.makeMove(new Move(1, 0, 2)));
        assertTrue(mGame.makeMove(new Move(2, 1, 1)));
        assertTrue(mGame.makeMove(new Move(1, 0, 1)));

        testMove = new Move(2, 2, 2);
        assertFalse(mGame.isDrawMove(testMove));
        assertTrue(mGame.makeMove(testMove));

        testMove = new Move(2, 2, 2);
        assertFalse(mGame.isDrawMove(testMove));

        int[][] board = new int[3][3];
        Move move;
        TicTacToeGame game;

        board[0][0] = 2;
        board[0][1] = 1;
        board[0][2] = 2;

        board[1][0] = 1;
        board[1][1] = 1;
        board[1][2] = 2;

        board[2][0] = 1;
        board[2][1] = 2;
        board[2][2] = 0;

        game = new TicTacToeGame(board);
        move = new TicTacToeGame.Move(1, 2, 2);
        assertTrue(game.isDrawMove(move));

        // Second test
        board[0][0] = 0;
        board[0][1] = 0;
        board[0][2] = 0;

        board[1][0] = 2;
        board[1][1] = 1;
        board[1][2] = 1;

        board[2][0] = 1;
        board[2][1] = 0;
        board[2][2] = 2;

        game = new TicTacToeGame(board);

        move = new TicTacToeGame.Move(2, 0, 2);
        assertFalse(game.isDrawMove(move));
    }
}