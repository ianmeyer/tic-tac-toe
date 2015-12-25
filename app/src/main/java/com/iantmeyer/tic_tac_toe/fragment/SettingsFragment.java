package com.iantmeyer.tic_tac_toe.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.iantmeyer.tic_tac_toe.R;
import com.iantmeyer.tic_tac_toe.activity.TicTacToeActivity;
import com.iantmeyer.tic_tac_toe.game.TicTacToeGame;
import com.iantmeyer.tic_tac_toe.util.BusProvider;
import com.iantmeyer.tic_tac_toe.util.SettingsUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Fragment for managing player order and type
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private static final int PLAYER_X = 1;
    private static final int PLAYER_O = 2;

    private boolean ignoreSpinnerXItemSelection = false;
    private boolean ignoreSpinnerOItemSelection = false;

    @Bind(R.id.player_x_layout)
    protected LinearLayout mPlayerXLayout;

    @Bind(R.id.player_o_layout)
    protected LinearLayout mPlayerOLayout;

    @Bind(R.id.player_layout_container)
    protected LinearLayout mPlayerLayoutContainer;

    @Bind(R.id.player_x_type)
    protected Spinner mPlayerXTypeSpinner;

    @Bind(R.id.player_o_type)
    protected Spinner mPlayerOTypeSpinner;

    public SettingsFragment() {
        // Fragments should have empty constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    private TicTacToeGame getGame() {
        return ((TicTacToeActivity) getActivity()).getGame();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);
        BusProvider.INSTANCE.getBus().register(this);

        notifyFirstPlayerChange(getGame().getNextPlayer());   // TODO IM -????
        notifyPlayerTypeChange(PLAYER_X, getGame().isPlayerHuman(PLAYER_X));
        notifyPlayerTypeChange(PLAYER_O, getGame().isPlayerHuman(PLAYER_O));

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BusProvider.INSTANCE.getBus().unregister(this);
    }

    @OnClick({R.id.swap_order_image_view, R.id.player_x_image_view, R.id.player_o_image_view})
    void swapClicked() {
        int firstPlayer = SettingsUtil.getFirstPlayer();
        int newFirstPlayer = (1 == firstPlayer) ? 2 : 1;
        SettingsUtil.setFirstPlayer(newFirstPlayer);
        this.notifyFirstPlayerChange(newFirstPlayer);

        if (getGame().getState().equals(TicTacToeGame.State.SET_UP)) {
            getGame().resetGame(newFirstPlayer);
        }
    }

    private void notifyFirstPlayerChange(int firstPlayer) {
        if (firstPlayer == PLAYER_X) {
            mPlayerLayoutContainer.removeView(mPlayerOLayout);
            mPlayerLayoutContainer.addView(mPlayerOLayout);
        } else if (firstPlayer == PLAYER_O) {
            mPlayerLayoutContainer.removeView(mPlayerXLayout);
            mPlayerLayoutContainer.addView(mPlayerXLayout);
        }
    }

    @OnItemSelected(R.id.player_x_type)
    void onPlayerXTypeSelected(int position) {
        if (ignoreSpinnerXItemSelection) {
            ignoreSpinnerXItemSelection = false;
            return;
        }
        boolean human = (position == 0);
        boolean oldSetting = SettingsUtil.isPlayerHuman(PLAYER_X);
        if (human == oldSetting) {
            // No change
            return;
        }
        SettingsUtil.setPlayerHuman(PLAYER_X, human);
        getGame().setPlayerHuman(PLAYER_X, human);

        boolean otherPlayerComp = !SettingsUtil.isPlayerHuman(PLAYER_O);
        if (!human && otherPlayerComp) {
            TwoComputerPlayersToast();
            mPlayerOTypeSpinner.setSelection(0);
            return;
        }
        getGame().resetGame(SettingsUtil.getFirstPlayer());
    }

    @OnItemSelected(R.id.player_o_type)
    void onPlayerOTypeSelected(int position) {
        if (ignoreSpinnerOItemSelection) {
            ignoreSpinnerOItemSelection = false;
            return;
        }
        boolean human = (position == 0);
        boolean oldSetting = SettingsUtil.isPlayerHuman(PLAYER_O);
        if (human == oldSetting) {
            // No change
            return;
        }
        SettingsUtil.setPlayerHuman(PLAYER_O, human);
        getGame().setPlayerHuman(PLAYER_O, human);

        boolean otherPlayerComp = !SettingsUtil.isPlayerHuman(PLAYER_X);
        if (!human && otherPlayerComp) {
            TwoComputerPlayersToast();
            mPlayerXTypeSpinner.setSelection(0);
            return;
        }
        getGame().resetGame(SettingsUtil.getFirstPlayer());
    }

    private void notifyPlayerTypeChange(int player, boolean human) {
        int selection = human ? 0 : 1;
        if (player == PLAYER_X) {
            ignoreSpinnerXItemSelection = true;
            mPlayerXTypeSpinner.setSelection(selection);
        } else if (player == PLAYER_O) {
            ignoreSpinnerOItemSelection = true;
            mPlayerOTypeSpinner.setSelection(selection);
        }
    }

    private void TwoComputerPlayersToast() {
        Toast toast = Toast.makeText(getContext(), "There can only be one computer player!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
