package com.frybynite.magicdress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Admin on 3/8/2017.
 */

public class WandActions {
    public static final String ACTION_CONNECTED   = "com.frybynite.magicdress.wand.CONNECT";
    public static final String ACTION_DISCONNECT  = "com.frybynite.magicdress.wand.DISCONNECT";
    public static final String ACTION_MOVE_DOWN   = "com.frybynite.magicdress.wand.MOVE_DOWN";
    public static final String ACTION_MOVE_FLAT   = "com.frybynite.magicdress.wand.MOVE_FLAT";
    public static final String ACTION_MOVE_ROTATE = "com.frybynite.magicdress.wand.MOVE_ROTATE";
    public static final String ACTION_MOVE_THROW  = "com.frybynite.magicdress.wand.MOVE_THROW";

    public static final String ACTION_SHIMMER = "com.frybynite.magicdress.action.SHIMMER";
    public static final String ACTION_TWINKLE = "com.frybynite.magicdress.action.TWINKLE";
    public static final String ACTION_LIGHTNING = "com.frybynite.magicdress.action.LIGHTNING";
    public static final String ACTION_RAINBOW = "com.frybynite.magicdress.action.RAINBOW";
    public static final String ACTION_RAINBOW_NO_PULSE = "com.frybynite.magicdress.action.RAINBOW_NO_PULSE";
    public static final String ACTION_WHITE_SLIDE = "com.frybynite.magicdress.action.WHITE_SLIDE";
    public static final String ACTION_DARK = "com.frybynite.magicdress.action.DARK";
    public static final String ACTION_TWINKLE_LIGHT = "com.frybynite.magicdress.action.TWINKLE_LIGHT";
    public static final String ACTION_MAIZE_AND_BLUE = "com.frybynite.magicdress.action.MAIZE_AND_BLUE";
    public static final String ACTION_PULSE_ALL = "com.frybynite.magicdress.action.PULSE_ALL";
    public static final String ACTION_PULSE_RIGHT = "com.frybynite.magicdress.action.PULSE_RIGHT";
    public static final String ACTION_PULSE_LEFT = "com.frybynite.magicdress.action.PULSE_LEFT";
    public static final String ACTION_MOSTLY_WHITE= "com.frybynite.magicdress.action.MOSTLY_WHITE";


    /**
     * Registers a broadcast wandReceiver for all actions.
     * @param context
     * @param receiver
     */
    public static void registerReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter(ACTION_CONNECTED);
        filter.addAction(ACTION_DISCONNECT);
        filter.addAction(ACTION_MOVE_DOWN);
        filter.addAction(ACTION_MOVE_FLAT);
        filter.addAction(ACTION_MOVE_ROTATE);
        filter.addAction(ACTION_MOVE_THROW);
        filter.addAction(ACTION_SHIMMER);
        filter.addAction(ACTION_TWINKLE);
        filter.addAction(ACTION_LIGHTNING);
        filter.addAction(ACTION_RAINBOW);
        filter.addAction(ACTION_RAINBOW_NO_PULSE);
        filter.addAction(ACTION_WHITE_SLIDE);
        filter.addAction(ACTION_TWINKLE_LIGHT);
        filter.addAction(ACTION_DARK);
        filter.addAction(ACTION_MAIZE_AND_BLUE);
        filter.addAction(ACTION_PULSE_ALL);
        filter.addAction(ACTION_PULSE_LEFT);
        filter.addAction(ACTION_PULSE_RIGHT);
        filter.addAction(ACTION_MOSTLY_WHITE);

        mgr.registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
        mgr.unregisterReceiver(receiver);
    }


}
