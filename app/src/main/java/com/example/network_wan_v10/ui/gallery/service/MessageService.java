package com.example.network_wan_v10.ui.gallery.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.network_wan_v10.R;
import com.example.network_wan_v10.ui.gallery.ChatActivity;
import com.example.network_wan_v10.ui.gallery.bean.BluetoothMessage;


/**
 * Created by xuhuan  0010.
 */
public class MessageService extends Service{
    private Context mContext;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter intentFilter = new IntentFilter(BluetoothMessage.ACTION_RECEIVED_NEW_MSG);
        intentFilter.setPriority(990);
        registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }


    public void sendNotification(String content,String remoteAddress,BluetoothMessage message){

        Intent chatIntent = new Intent(mContext, ChatActivity.class);
        chatIntent.putExtra("remoteAddress",remoteAddress);
        chatIntent.putExtra("lastmsg",message);


        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(mContext)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("您有一条新消息")
                .build();


        notification.defaults = Notification.DEFAULT_SOUND;
        notificationManager.notify(1,notification);
    }


    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothMessage bluetoothMessage = (BluetoothMessage) intent.getSerializableExtra("msg");
            bluetoothMessage.setIsMe(0);

            String remoteAddress = bluetoothMessage.getSender();

            String msgContent;
            if(bluetoothMessage.getContent().length()>10){
                msgContent = bluetoothMessage.getContent().substring(0,10);
            }else {
                msgContent = bluetoothMessage.getContent();
            }
            String msg = bluetoothMessage.getSenderNick()+":"+msgContent;

            sendNotification(msg,remoteAddress,bluetoothMessage);
        }
    };
}
