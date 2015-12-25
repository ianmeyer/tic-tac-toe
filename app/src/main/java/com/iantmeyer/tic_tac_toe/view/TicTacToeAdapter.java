package com.iantmeyer.tic_tac_toe.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iantmeyer.tic_tac_toe.R;
import com.iantmeyer.tic_tac_toe.game.TicTacToeGame;
import com.iantmeyer.tic_tac_toe.util.BusProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Display a 3x3 tic-tac-toe board
 */
public class TicTacToeAdapter extends RecyclerView.Adapter {

    private TicTacToeGame mGame;

    /**
     * Display a 3x3 tic-tac-toe board in a recycler view
     *
     * @param game
     */
    public TicTacToeAdapter(TicTacToeGame game) {
        mGame = game;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tile_tic_tac_toe, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int row = (int) Math.floor(position / mGame.getBoardSize());
        int column = position % mGame.getBoardSize();

        switch (mGame.getPlayerAtPosition(row, column)) {
            case 0:
                ((ViewHolder) holder).mImageView.setImageDrawable(null);
                break;
            case 1:
                ((ViewHolder) holder).mImageView.setImageResource(R.drawable.tic_tac_toe_mark_x);
                break;
            case 2:
                ((ViewHolder) holder).mImageView.setImageResource(R.drawable.tic_tac_toe_mark_o);
                break;
        }
        ((ViewHolder) holder).mRow = row;
        ((ViewHolder) holder).mColumn = column;
        /*
        if ((row + column)% 2 == 1) {
            ((ViewHolder) holder).mImageView.setBackgroundColor(Color.LTGRAY);
        }
        */
    }

    @Override
    public int getItemCount() {
        return (int) Math.pow(mGame.getBoardSize(), 2);
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tic_tac_toe_tile_image_view)
        protected ImageView mImageView;
        public int mRow;
        public int mColumn;

        public ViewHolder(View rootView) {
            super(rootView);
            ButterKnife.bind(this, rootView);
        }

        @OnClick(R.id.tic_tac_toe_tile)
        public void onClick() {
            BusProvider.INSTANCE.getBus().post(new BoardClickEvent(mRow, mColumn));
        }
    }

    /**
     * Otto click event triggered when the user clicks on the game board
     */
    public static class BoardClickEvent {
        private int mRow;
        private int mColumn;

        public BoardClickEvent(int row, int column) {
            this.mRow = row;
            this.mColumn = column;
        }

        public int getRow() {
            return mRow;
        }

        public int getColumn() {
            return mColumn;
        }
    }
}