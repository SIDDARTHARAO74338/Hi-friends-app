package com.siddartharao.hifriends;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // Screen is turned off, start the Main2Activity
            ((Activity) context).finish();
            Intent startMain2ActivityIntent = new Intent(context, MainActivity.class);
            startMain2ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain2ActivityIntent);
        }
    }
}
