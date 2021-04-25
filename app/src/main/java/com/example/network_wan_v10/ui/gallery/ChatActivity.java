package com.example.network_wan_v10.ui.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.network_wan_v10.R;
import com.example.network_wan_v10.ui.gallery.adapter.MessageListAdapter;
import com.example.network_wan_v10.ui.gallery.bean.BluetoothMessage;
import com.example.network_wan_v10.ui.gallery.database.DBManager;
import com.example.network_wan_v10.ui.gallery.manager.UserManager;
import com.example.network_wan_v10.ui.gallery.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private TextView title;
    private EditText editText;
    private ListView msgListView;
    private List<BluetoothMessage> msgList;
    private MessageListAdapter adapter;

    private String remoteAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        title = (TextView) findViewById(R.id.title_tv);
        remoteAddress = getIntent().getStringExtra("remoteAddress");
        BluetoothMessage message = (BluetoothMessage) getIntent().getSerializableExtra("lastmsg");

        String name = BluetoothUtils.getInstance(this).getBluetoothAdapter().getRemoteDevice(remoteAddress).getName();
        title.setText(name);

        editText = (EditText) findViewById(R.id.edit_text);

        msgList = new ArrayList<>();
        msgListView = (ListView) findViewById(R.id.chat_listview);
        adapter = new MessageListAdapter(this,msgList);
        msgListView.setAdapter(adapter);

        IntentFilter intentFilter = new IntentFilter(BluetoothMessage.ACTION_RECEIVED_NEW_MSG);
        intentFilter.setPriority(999);
        registerReceiver(msgReceiver, intentFilter);


        List<BluetoothMessage> list = DBManager.findAll(remoteAddress);
        if(list!=null){
            msgList.addAll(list);
            adapter.notifyDataSetChanged();
        }

        msgListView.setSelection(msgList.size());


    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                onBackPressed();
                break;
            case R.id.btn_send:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String msg = editText.getText().toString();
        if(TextUtils.isEmpty(msg)){
            Toast.makeText(this, "消息内容不能为空!", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
        BluetoothMessage bluetoothMessage = new BluetoothMessage();
        bluetoothMessage.setContent(msg);
        //自己的昵称
        String nickName = UserManager.getMyUser(this).getNickName();
        nickName = TextUtils.isEmpty(nickName) ? BluetoothUtils.getInstance(this).getBluetoothAdapter().getName():nickName;
        bluetoothMessage.setSenderNick(nickName);
        bluetoothMessage.setSender(UserManager.getMyUser(this).getUserId());
        bluetoothMessage.setIsMe(1);
        BluetoothUtils.getInstance(this).sendMessageHandle(bluetoothMessage, remoteAddress);
        addMessage(bluetoothMessage);

        editText.setText("");
    }

    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothMessage bluetoothMessage = (BluetoothMessage) intent.getSerializableExtra("msg");
            bluetoothMessage.setIsMe(0);

            String sender = bluetoothMessage.getSender();
            if(remoteAddress.equals(sender)){
                addMessage(bluetoothMessage);
                abortBroadcast();
            }

        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }

    public void addMessage(BluetoothMessage message){
        msgList.add(message);
        adapter.notifyDataSetChanged();
        msgListView.setSelection(msgList.size()-1);
    }
}
