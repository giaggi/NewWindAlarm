/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwind.windalarm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static gwind.windalarm.CommonUtilities.sendMessageToMainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService/*GcmListenerService*/ {

    private static final String TAG = "WindAlarmGcmListener";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(TAG, "RICEVUTO MESSAGGGIO________________**************************************************");
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

            try {
                JSONObject data = new JSONObject(remoteMessage.getData());
                String notificationType = data.getString("notificationtype");
                // notifies user
                if (notificationType != null && notificationType.equals("Alarm")) {

                    Log.i(TAG, "Alarm received");
                    playAlarm(getApplicationContext(), data);
                    return;
                } else {

                    String spotId = data.getString("spotID");
                    //if (spotId != null) {
                    if (!AlarmPreferences.getHighWindNotification(getApplicationContext())
                            || !AlarmPreferences.getWindIncreaseNotification(getApplicationContext())) {

                        Log.i(TAG, "Notification disabled");
                        return;
                    }
                    // TODO riaggiungere controllo notifiche non favorites

                    String title = data.getString("title");
                    String message = data.getString("message");
                    //CommonUtilities.sendMessageToMainActivity(getApplicationContext(), title, "messagetext", notificationType); // questto fa in modo che venga mandato un messaggio alla main actrivitik che poi puo fare qualcosa in base al tipo
                    //generateUINotification(getApplicationContext(), message, title); // questo genera la notifica nella barra notifica
                    //notifyUser(getApplicationContext(), message, title, spotId);

                    //}
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }





    //@Override
    public void _onMessageReceived(String from, Bundle data) {
        Log.i(TAG, "onMessageReceived");
        String message = data.getString("message");
        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        String notificationType = data.getString("notificationtype");
        // notifies user
        if (notificationType != null && notificationType.equals("Alarm")) {

            Log.i(TAG, "Alarm received");
            //playAlarm(getApplicationContext(), data);

            return;

        } else {

            String spotId = data.getString("spotID");
            //if (spotId != null) {
                if (!AlarmPreferences.getHighWindNotification(getApplicationContext())
                        || !AlarmPreferences.getWindIncreaseNotification(getApplicationContext())) {

                    Log.i(TAG, "Notification disabled");
                    return;
                }
                // TODO riaggiungere controllo notifiche non favorites
                /*if (!AlarmPreferences.isSpotFavorite(getApplicationContext(), Integer.valueOf(spotId))) {
                    Log.i(TAG, "spot is not favorites");
                    return;
                }*/


                String title = data.getString("title");
                message = data.getString("message");
                //CommonUtilities.sendMessageToMainActivity(getApplicationContext(), title, "messagetext", notificationType); // questto fa in modo che venga mandato un messaggio alla main actrivitik che poi puo fare qualcosa in base al tipo
                //generateUINotification(getApplicationContext(), message, title); // questo genera la notifica nella barra notifica
                //notifyUser(getApplicationContext(), message, title, spotId);

            //}
        }


        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(message);
        //generateUINotification(getApplicationContext(), message, "GWindAlarm"); // questo genera la notifica nella barra notifica
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("GWindAlarm")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void playAlarm(Context context, JSONObject alarmData) {
        Intent resultIntent = new Intent(context, PlayAlarmActivity.class);

        String spotId = null;
        try {
            spotId = alarmData.getString("spotID");
            String spotName = alarmData.getString("spotName");
            String alarmId = alarmData.getString("alarmId");
            String curDate = alarmData.getString("curDate");
            String curspeed = alarmData.getString("curspeed");
            String curavspeed = alarmData.getString("curavspeed");
            String windid = alarmData.getString("windid");

            Bundle b = new Bundle();
            b.putString("spotid", spotId);
            b.putString("spotName", spotName);
            b.putString("alarmid", alarmId);
            b.putString("curspeed", curspeed);
            b.putString("curavspeed", curavspeed);
            b.putString("curdate", curDate);
            b.putString("windid", windid);
            resultIntent.putExtras(b); //Put your id to your next Intent

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(resultIntent);
        /*generateUINotification(getApplicationContext(), curDate.toString()
                + "\nSveglia vento attivata"
                + "\nIntensità vento " + curspeed
                + "\nIntensità media " + curavspeed
                + "\nwindid " + windid,
                "" + spotName);*/
            /*notifyUser(SplashActivity.getInstance(), curDate.toString()
                            + "\nSveglia vento attivata"
                            + "\nIntensità vento " + curspeed
                            + "\nIntensità media " + curavspeed
                            + "\nwindid " + windid,
                    "" + spotName, spotId);*/
            //sendMessageToMainActivity(getApplicationContext(), "title", "messagetext", notificationType); // questto fa in modo che venga mandato un messaggio alla main actrivitik che poi puo fare qualcosa in base al tipo

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Issues a notification to inform the user that server has sent a message. // cioe mostra un messaggio nella barra notifiche e fa in modo
     * che venga aperta una activiti se l'utente clicca sulla notifica
     */
    private void generateUINotification(Context context, String message, String title) {

        int icon = R.drawable.logo;

        Intent resultIntent = new Intent(context, MainActivity.class);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                //.setContentIntent(resultIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(icon)
                .setContentText(message).build();



        // Creates an explicit intent for an Activity in your app
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);  // questo fa in modo che venga aperta l'activiti quando l'user clicca sulla notifica
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, notification/*mBuilder.build()*/);
    }

    public static void xnotifyUser(Context context, String header,
                                  String message, String spotId) {

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(
                context.getApplicationContext(), SplashActivity.class);

        notificationIntent.putExtra("spotId",spotId);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(header)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pIntent).setAutoCancel(true)
                .setSmallIcon(R.drawable.logo).build();

        // mId allows you to update the notification later on.
        int mId = 2;
        notificationManager.notify(mId, notification);
    }

}
