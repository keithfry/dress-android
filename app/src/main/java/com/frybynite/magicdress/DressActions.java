package com.frybynite.magicdress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Admin on 3/8/2017.
 */

public class DressActions {
    public static final String ACTION_CONNECTED   = "com.frybynite.magicdress.dress.CONNECT";
    public static final String ACTION_DISCONNECT  = "com.frybynite.magicdress.dress.DISCONNECT";
    public static final String ACTION_SCANNING  = "com.frybynite.magicdress.dress.SCANNING";
    public static final String ACTION_MESSAGE = "com.frybynite.magicdress.dress.MESSAGE";
    /**
     * Registers a broadcast wandReceiver for all actions.
     * @param context
     * @param receiver
     */
    public static void registerReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter(ACTION_CONNECTED);
        filter.addAction(ACTION_DISCONNECT);
        filter.addAction(ACTION_SCANNING);
        filter.addAction(ACTION_MESSAGE);
        mgr.registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
        mgr.unregisterReceiver(receiver);
    }


}
