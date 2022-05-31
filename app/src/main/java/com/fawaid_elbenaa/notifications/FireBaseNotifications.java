package com.fawaid_elbenaa.notifications;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_home.HomeActivity;
import com.fawaid_elbenaa.activities_fragments.activity_packages.PackagesActivity;
import com.fawaid_elbenaa.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.fawaid_elbenaa.models.StatusResponse;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.share.App;
import com.fawaid_elbenaa.tags.Tags;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FireBaseNotifications extends FirebaseMessagingService {
    private Map<String, String> map;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        map = remoteMessage.getData();

        for (String key : map.keySet()) {
            Log.e("Key=", key + "_value=" + map.get(key));
        }

        manageNotification(map);


    }

    private void manageNotification(Map<String, String> map) {
        String title = map.get("title");
        String body = map.get("body");
        String notification_type = map.get("type");


        String sound_Path = "";
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        sound_Path = uri.toString();
        Intent cancelIntent = new Intent(this, BroadcastCancelNotification.class);
        PendingIntent cancelPending = PendingIntent.getBroadcast(this, 0, cancelIntent, 0);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setAutoCancel(true)
                .setOngoing(false)
                .setChannelId(App.CHANNEL_ID)
                .setDeleteIntent(cancelPending)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        notificationCompat.setContentTitle(title);
        notificationCompat.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(body)));
        Intent intent = null;
        if (notification_type.equals("package")) {
            intent = new Intent(this, PackagesActivity.class);

        } else if (notification_type.equals("ads")) {
            intent = new Intent(this, ProductDetailsActivity.class);
            String ads_id = map.get("ad_id");
            intent.putExtra("product_id", Integer.parseInt(ads_id));

        } else {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra("firebase", true);

        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        notificationCompat.setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
        manager.notify(Tags.not_id, notificationCompat.build());

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (getUserModel() == null) {
            return;
        }

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {
                    UserModel userModel = getUserModel();
                    Api.getService(Tags.base_url)
                            .updateFirebaseToken(token, userModel.getData().getId(), "android")
                            .enqueue(new Callback<StatusResponse>() {
                                @Override
                                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {

                                        if (response.body().getStatus() == 200) {
                                            userModel.getData().setFirebaseToken(token);
                                            setUserModel(userModel);
                                            Log.e("token", "updated successfully");

                                        }
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<StatusResponse> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                //         Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                //       Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });
    }

    public UserModel getUserModel() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getUserData(this);
    }


    public void setUserModel(UserModel userModel) {
        Preferences preferences = Preferences.getInstance();
        preferences.create_update_userdata(this, userModel);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
