package com.iantmeyer.tic_tac_toe.activity;

import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.iantmeyer.tic_tac_toe.R;
import com.iantmeyer.tic_tac_toe.fragment.SettingsFragment;
import com.iantmeyer.tic_tac_toe.fragment.TicTacToeFragment;
import com.iantmeyer.tic_tac_toe.game.TicTacToeGame;
import com.iantmeyer.tic_tac_toe.util.BusProvider;
import com.iantmeyer.tic_tac_toe.util.FragmentHelper;
import com.iantmeyer.tic_tac_toe.util.SettingsUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TicTacToeActivity extends AppCompatActivity
        implements TicTacToeGame.GameProvider, Toolbar.OnMenuItemClickListener {

    private static final String TAG = "TicTacToeActivity";

    private static TicTacToeGame mGame;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_tic_tac_toe_landscape);
        } else {
            setContentView(R.layout.activity_tic_tac_toe_portrait);
        }
        ButterKnife.bind(this);

        BusProvider.INSTANCE.getBus().register(this);

        if (mGame == null) {
            Log.i(TAG, "Initialize game object");
            int[][] savedBoard = SettingsUtil.getGameBoard(3);
            mGame = new TicTacToeGame(savedBoard);
            mGame.setPlayerHuman(1, SettingsUtil.isPlayerHuman(1));
            mGame.setPlayerHuman(2, SettingsUtil.isPlayerHuman(2));
        }

        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setTitle(getTitle());

        if (savedInstanceState == null) {
            FragmentHelper.replace(this, TicTacToeFragment.newInstance(), R.id.fragment_container_game);
            FragmentHelper.replace(this, SettingsFragment.newInstance(), R.id.fragment_container_settings);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SettingsUtil.saveGameBoard(mGame.getBoard());

        BusProvider.INSTANCE.getBus().unregister(this);
    }

    /**
     * Getter for the Tic-tac-toe game
     * <br><br>
     * Supplies the game to the child fragments of this activity
     *
     * @return
     */
    @Override
    public TicTacToeGame getGame() {
        return mGame;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset_game:
                mGame.resetGame(SettingsUtil.getFirstPlayer());
                return true;
            case R.id.menu_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String msg = "created by Ian Meyer" +
                        "<br/><br/>" +
                        "<a href='https://github.com/ianmeyer/tic-tac-toe'>source on GitHub</a>";
                builder.setMessage(Html.fromHtml(msg));
                AppCompatDialog dialog = builder.create();
                dialog.show();
                ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                return true;
            default:
                return false;
        }
    }
}
