package com.example.network_wan_v10.ui.gallery;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.network_wan_v10.R;
import com.example.network_wan_v10.ui.gallery.adapter.DevicesAdapter;
import com.example.network_wan_v10.ui.gallery.bean.BluetoothMessage;
import com.example.network_wan_v10.ui.gallery.utils.BluetoothUtils;
import com.example.network_wan_v10.ui.gallery.utils.ProgressUtils;

import java.util.ArrayList;

public class GalleryFragment extends Fragment  implements AdapterView.OnItemClickListener{

    private GalleryViewModel galleryViewModel;
    private Button btn_control;
    private Button btn_play;
    private Button btn_pause;
    private Button btn_stop;
    private boolean isStart = false;
    private MediaRecorder mr = null;
    private TextView tv_show;
    private MediaPlayer mediaPlayer = null;
    private boolean isRelease = true;   //判断是否MediaPlayer是否释放的标志
    public static final String[] permissions = {
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.BLUETOOTH_PRIVILEGED"

    };
    private ListView listView;
    private DevicesAdapter adapter;
    private ArrayList<BluetoothDevice> list;
    private Context mContext;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        mContext = getActivity();
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(permissions,1);
        }

        initReceiver();

        list = new ArrayList<>();
        adapter = new DevicesAdapter(getActivity(),list);
        listView = (ListView)root.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        if(BluetoothUtils.getInstance(getActivity()).isEnabled()){
//            ProgressUtils.showProgress(mContext, "正在初始化...");
            BluetoothUtils.getInstance(getActivity()).scanDevices();
        }else {
            BluetoothUtils.getInstance(getActivity()).enableBluetooth();
        }

        list.addAll(BluetoothUtils.getInstance(mContext).getAvailableDevices());
        adapter.notifyDataSetChanged();


        return root;
    }




    public void onClick(View view){
        showToast("开始扫描");
        list.clear();
        list.addAll(BluetoothUtils.getInstance(mContext).getAvailableDevices());
        adapter.notifyDataSetChanged();
        BluetoothUtils.getInstance(getActivity()).scanDevices();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        filter.addAction(BluetoothMessage.ACTION_INIT_COMPLETE);
        filter.addAction(BluetoothMessage.ACTION_CONNECTED_SERVER);
        filter.addAction(BluetoothMessage.ACTION_CONNECT_ERROR);
        mContext.registerReceiver(receiver, filter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    for(int i=0;i<list.size();i++){
                        if(device.getAddress()==null || device.getAddress().equals(list.get(i).getAddress())){
                            return;
                        }
                    }
                    list.add(device);
                    adapter.notifyDataSetChanged();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                    showToast("开始扫描");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    showToast("扫描完成");
                    break;
                case BluetoothMessage.ACTION_INIT_COMPLETE:
                    ProgressUtils.dismissProgress();
                    break;
                case BluetoothMessage.ACTION_CONNECTED_SERVER:
                    ProgressUtils.dismissProgress();
                    String remoteAddress = intent.getStringExtra(BluetoothUtils.EXTRA_REMOTE_ADDRESS);
                    openChatRoom(remoteAddress);
                    break;
                case BluetoothMessage.ACTION_CONNECT_ERROR:
                    ProgressUtils.dismissProgress();
                    showToast(intent.getStringExtra(BluetoothUtils.EXTRA_ERROR_MSG));
                    break;
            }
        }
    };

    private void openChatRoom(String remoteAddress) {
        Intent intent = new Intent(mContext,ChatActivity.class);
        intent.putExtra("remoteAddress",remoteAddress);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void showToast(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = list.get(position);
        if(BluetoothUtils.getInstance(mContext).isDiscoverying()){
            BluetoothUtils.getInstance(mContext).cancelScan();
        }

        BluetoothUtils.getInstance(mContext).connect(device.getAddress());

        ProgressUtils.showProgress(mContext,"正在连接，请稍候...");
    }

}























//        btn_control = (Button)root.findViewById(R.id.btn_control);
//                tv_show = (TextView)root.findViewById(R.id.tv_show);
//                btn_play= (Button)root.findViewById(R.id.btn_play);
//                btn_pause= (Button)root.findViewById(R.id.btn_pause);
//                btn_stop= (Button)root.findViewById(R.id.btn_stop);
//
//                btn_control.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        if(!isStart){
//        startRecord();
//        btn_control.setText("停止录制");
//        isStart = true;
//        }else{
//        stopRecord();
//        btn_control.setText("开始录制");
//        isStart = false;
//        }
//        }
//        });
//
//
//        btn_play.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        File dir = new File(Environment.getExternalStorageDirectory(),"sounds");
//        File soundFile = new File(dir,"current.mp3");
//        String myUri = soundFile.getAbsolutePath();
//
//        if (isRelease==true) {
//        mediaPlayer = new MediaPlayer();
//
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////            mPlayer = MediaPlayer.create(this,R.raw.xzq);
//        try {
//        mediaPlayer.setDataSource(myUri);
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        try {
//        mediaPlayer.prepare();
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        mediaPlayer.start();
//        isRelease = false;
//        tv_show.setText(myUri);
//
//        }
//        }
//        });
//
//
//        btn_pause.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        mediaPlayer.pause();     //停止播放
//        }
//        });
//
//        btn_stop.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        mediaPlayer.reset();
//        //重置MediaPlayer
//        mediaPlayer.release();   //释放MediaPlayer
//        isRelease = true;            }
//        });
//
//        return root;
//        }
//
//
//
//
//
////开始录制
//private void startRecord(){
//        if(mr == null){
//        File dir = new File(Environment.getExternalStorageDirectory(),"sounds");
//        if(!dir.exists()){
//        dir.mkdirs();
//        }
//        File soundFile = new File(dir,"current.mp3");
//        if(!soundFile.exists()){
//        try {
//        soundFile.createNewFile();
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//
//        }
//        mr = new MediaRecorder();
//        mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
//        mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
//        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
//        mr.setOutputFile(soundFile.getAbsolutePath());
//        String myUri = soundFile.getAbsolutePath();
//        tv_show.setText(myUri);
//        try {
//        mr.prepare();
//        mr.start();  //开始录制
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//
//        }
//        }
//
////停止录制，资源释放
//private void stopRecord(){
//        if(mr != null){
//        mr.stop();
//        mr.release();
//        mr = null;
//        }
//        }
//        }
