package com.cse.its.its;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CustomNotification extends Activity {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager notificMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificMgr.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        finish();
    }

    /**
     * Dismiss the notification when Dismiss is clicked.
     * @param nid
     * @param context
     * @return empty Intent
     */
    public static PendingIntent dismissNotification(int nid, Context context) {
        // Empty intent as we do not want it to do anything.
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, nid);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * Close the Application when OK is clicked in Notification.
     * @param nid
     * @param context
     * @return Intent
     */

    public static PendingIntent acceptNotification(int nid, Context context) {
        Intent intent = new Intent(context, CustomNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, nid);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
