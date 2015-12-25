package com.iantmeyer.tic_tac_toe.util;

import com.squareup.otto.Bus;

public enum BusProvider {
    INSTANCE;

    private final Bus mBus;

    /**
     * BusProvider Singleton
     * <br><br>
     * Provides access to a single eventbus instance throughout the app
     */
    BusProvider() {
        mBus = new Bus();
    }

    public Bus getBus() {
        return mBus;
    }
}
