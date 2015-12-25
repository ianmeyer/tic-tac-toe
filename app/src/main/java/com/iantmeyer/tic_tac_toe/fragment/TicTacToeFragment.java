package com.iantmeyer.tic_tac_toe.fragment;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iantmeyer.tic_tac_toe.R;
import com.iantmeyer.tic_tac_toe.activity.TicTacToeActivity;
import com.iantmeyer.tic_tac_toe.game.GameAi;
import com.iantmeyer.tic_tac_toe.game.TicTacToeGame;
import com.iantmeyer.tic_tac_toe.util.BusProvider;
import com.iantmeyer.tic_tac_toe.util.SettingsUtil;
import com.iantmeyer.tic_tac_toe.util.ViewUtil;
import com.iantmeyer.tic_tac_toe.view.TicTacToeAdapter;

import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment containing a Tic-Tac-Toe game board
 */
public class TicTacToeFragment extends Fragment {

    private static final String TAG = "TicTacToeFragment";

    @Bind(R.id.tic_tac_toe_overlay_image_view)
    protected ImageView mOverlayIamgeView;

    @Bind(R.id.tic_tac_toe_recycler_view)
    protected RecyclerView mRecyclerView;

    private GridLayoutManager mLayoutManager;
    private TicTacToeAdapter mAdapter;

    private int mBoardDim;
    private Snackbar mSnackbar;

    private static final String[] PLAYERS = {"X", "O"};

    public TicTacToeFragment() {
        // Fragments should have empty constructor
    }

    public static TicTacToeFragment newInstance() {
        TicTacToeFragment f = new TicTacToeFragment();
        return f;
    }

    private TicTacToeGame getGame() {
        return ((TicTacToeActivity) getActivity()).getGame();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tic_tac_toe, container, false);
        ButterKnife.bind(this, rootView);

        BusProvider.INSTANCE.getBus().register(this);

        mBoardDim = getAvailableHeight();

        // Adapter
        mAdapter = new TicTacToeAdapter(getGame());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(mBoardDim, mBoardDim));
        mRecyclerView.setBackgroundResource(R.drawable.tic_tac_tie_grid_600);

        mLayoutManager = new GridLayoutManager(getContext(), getGame().getBoardSize());
        mRecyclerView.setLayoutManager(mLayoutManager);

        setOverlay();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setSnackbar();
        setOverlay();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        BusProvider.INSTANCE.getBus().unregister(this);
    }

    @Subscribe
    public void onGridItemPressed(TicTacToeAdapter.BoardClickEvent boardClickEvent) {
        int row = boardClickEvent.getRow();
        int column = boardClickEvent.getColumn();
        int player = getGame().getNextPlayer();
        if (getGame().isPlayerHuman(player)) {
            TicTacToeGame.Move move = new TicTacToeGame.Move(player, row, column);
            getGame().makeMove(move);
        }
    }

    @Subscribe
    public void onMoveMade(TicTacToeGame.GameMoveEvent gameMoveEvent) {
        mAdapter.notifyDataSetChanged();

        setSnackbar();
        setOverlay();
    }

    @Subscribe
    public void onGameAiStartEvent(GameAi.AiStartEvent gameAiStartEvent) {
        setSnackbar();
    }

    @Subscribe
    public void onResetGame(TicTacToeGame.ResetGameEvent resetGameEvent) {
        setSnackbar();
        setOverlay();
        mOverlayIamgeView.setVisibility(View.INVISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    private int getAvailableHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        int statusBarHeight = ViewUtil.px(25);
        int snackBarHeight = ViewUtil.px(48);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return metrics.heightPixels - toolbarHeight - statusBarHeight - snackBarHeight;
        } else {
            return metrics.widthPixels;
        }
    }

    private void setSnackbar() {
        switch (getGame().getState()) {
            case PLAYER_1_WON:
                showGameOverSnackbar("Player " + PLAYERS[0] + " wins!");
                break;

            case PLAYER_2_WON:
                showGameOverSnackbar("Player " + PLAYERS[1] + " wins!");
                break;

            case DRAW:
                showGameOverSnackbar("Game is a draw!");
                break;

            case SET_UP:
            case IN_PROGRESS:
                int nextPlayer = getGame().getNextPlayer();
                boolean human = getGame().isPlayerHuman(nextPlayer);
                if (human) {
                    boolean bothHuman = getGame().isPlayerHuman(1) && getGame().isPlayerHuman(2);
                    String msg;
                    if(!bothHuman) {
                        msg = "Your turn!";
                    } else if (nextPlayer == 1) {
                        msg = "Player X's turn!";
                    } else {
                        msg = "Player O's turn!";
                    }
                    mSnackbar = Snackbar.make(this.getView(), msg, Snackbar.LENGTH_INDEFINITE);
                    mSnackbar.show();
                } else {
                    // Game Ai is running
                    mSnackbar = Snackbar.make(getView(), "Calculating the optimal move...", Snackbar.LENGTH_INDEFINITE);
                    mSnackbar.show();
                }
                break;
        }
    }

    private void showGameOverSnackbar(String msg) {
        mSnackbar = Snackbar.make(this.getView(), msg, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction("PLAY AGAIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getGame().resetGame(SettingsUtil.getFirstPlayer());
                    }
                }
        );
        mSnackbar.show();
    }

    private void setOverlay() {
        Drawable drawable = null;
        int[][] board = getGame().getBoard();

        if (board[0][0] != 0 && board[0][0] == board[0][1] && board[0][1] == board[0][2]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_horizontal_1);

        } else if (board[1][0] != 0 && board[1][0] == board[1][1] && board[1][1] == board[1][2]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_horizontal_2);

        } else if (board[2][0] != 0 && board[2][0] == board[2][1] && board[2][1] == board[2][2]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_horizontal_3);

        } else if (board[0][0] != 0 && board[0][0] == board[1][0] && board[1][0] == board[2][0]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_vertical_1);

        } else if (board[0][1] != 0 && board[0][1] == board[1][1] && board[1][1] == board[2][1]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_vertical_2);

        } else if (board[0][2] != 0 && board[0][2] == board[1][2] && board[1][2] == board[2][2]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_vertical_3);

        } else if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_diagonal_1);

        } else if (board[2][0] != 0 && board[2][0] == board[1][1] && board[1][1] == board[0][2]) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.slash_diagonal_2);
        }
        if (drawable == null) {
            mOverlayIamgeView.setVisibility(View.INVISIBLE);

        } else {
            drawable.setTint(ContextCompat.getColor(getContext(), R.color.colorAccent));
            mOverlayIamgeView.setImageDrawable(drawable);
            mOverlayIamgeView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mBoardDim, mBoardDim);
            mOverlayIamgeView.setLayoutParams(params);
        }
    }
}
