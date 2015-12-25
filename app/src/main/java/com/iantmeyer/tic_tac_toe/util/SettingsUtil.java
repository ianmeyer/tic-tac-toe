package com.iantmeyer.tic_tac_toe.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.iantmeyer.tic_tac_toe.App;

/**
 * Created by ianmeyer on 12/21/15.
 */
public class SettingsUtil {
    private static final String FIRST_PLAYER = "first_player";
    private static final String PLAYER_1_HUMAN = "player_1_human";
    private static final String PLAYER_2_HUMAN = "player_2_human";
    private static final String GAME_BOARD = "game_board_#R#_#C#";

    private SettingsUtil() {
        // Don't allow instantiation
    }

    public static int getFirstPlayer() {
        return getInteger(FIRST_PLAYER, 1);
    }

    public static void setFirstPlayer(int player) {
        setInteger(FIRST_PLAYER, player);
    }

    public static boolean isPlayerHuman(int player) {
        if(player == 1) {
            return getBoolean(PLAYER_1_HUMAN, true);
        } else if(player == 2) {
            return getBoolean(PLAYER_2_HUMAN, false);
        }
        return true;
    }

    public static void setPlayerHuman(int player, boolean value) {
        if(player == 1) {
            setBoolean(PLAYER_1_HUMAN, value);
        } else if(player == 2) {
            setBoolean(PLAYER_2_HUMAN, value);
        }
    }

    public static int[][] getGameBoard(int size) {
        int[][] board = new int[size][size];
        for(int rowIdx = 0; rowIdx < size; rowIdx++) {
            for (int colIdx = 0; colIdx < size; colIdx++) {
                String key = GAME_BOARD.replace("#R#", "" + rowIdx).replace("#C#", "" + colIdx);
                board[rowIdx][colIdx] = getInteger(key, 0);
            }
        }
        return board;
    }

    public static void saveGameBoard(int[][] board) {
        for(int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            for (int colIdx = 0; colIdx < board[0].length; colIdx++) {
                String key = GAME_BOARD.replace("#R#", "" + rowIdx).replace("#C#", "" + colIdx);
                setInteger(key, board[rowIdx][colIdx]);
            }
        }
    }

    /*
        helper functions
     */
    private static void setBoolean(String preferenceName, boolean value) {
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putBoolean(preferenceName, value);
        edit.commit();
    }

    private static boolean getBoolean(String preferenceName, boolean defaultValue) {
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        return preferences.getBoolean(preferenceName, defaultValue);
    }

    private static void setInteger(String preferenceName, int value) {
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putInt(preferenceName, value);
        edit.commit();
    }

    private static int getInteger(String preferenceName, int defaultValue) {
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        return preferences.getInt(preferenceName, defaultValue);
    }
}
