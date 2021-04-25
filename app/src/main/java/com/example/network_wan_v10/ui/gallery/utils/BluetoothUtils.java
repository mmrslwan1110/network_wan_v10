package com.example.network_wan_v10.ui.gallery.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.network_wan_v10.ui.gallery.bean.BluetoothMessage;
import com.example.network_wan_v10.ui.gallery.database.DBManager;
import com.example.network_wan_v10.ui.gallery.service.MessageService;
import com.example.network_wan_v10.ui.gallery.service.ServerService;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by xuhuan 0007.
 */
public class BluetoothUtils {

    private static final String PROTOCOL_SCHEME_RFCOMM = "server_name";
    private static final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String EXTRA_REMOTE_ADDRESS = "remoteAddress";
    public static final String EXTRA_ERROR_MSG = "error_msg";
    private static BluetoothUtils instance;

    /** 已连接到服务器 */
    private static final int CONNECTED_SERVER = 1;
    /** 连接服务器出错 */
    private static final int CONNECT_SERVER_ERROR = CONNECTED_SERVER + 1;
    /** 正在连接服务器 */
    private static final int IS_CONNECTING_SERVER = CONNECT_SERVER_ERROR + 1;
    /** 等待客户端连接 */
    private static final int WAITING_FOR_CLIENT = IS_CONNECTING_SERVER + 1;
    /** 已连接客户端 */
    private static final int CONNECTED_CLIENT = WAITING_FOR_CLIENT + 1;
    /** 连接客户端出错 */
    private static final int CONNECT_CLIENT_ERROR = CONNECTED_CLIENT + 1;

    private BluetoothAdapter bluetoothAdapter;
    private Context mContext;

    /**
     * socket集合
     */
    private HashMap<String, BluetoothSocket> socketMap = new HashMap<>();
    /**
     * 远程设备集合
     */
//    private HashMap<String, BluetoothDevice> remoteDeviceMap = new HashMap<>();

    private HashMap<String,ReadThread> readThreadMap = new HashMap<>();

    private BluetoothServerSocket mServerSocket;

    private Handler linkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(msg.obj instanceof BluetoothMessage){
                BluetoothMessage message = (BluetoothMessage) msg.obj;
                Intent intent = new Intent();
                intent.setAction(BluetoothMessage.ACTION_RECEIVED_NEW_MSG);
                intent.putExtra("msg", message);
                mContext.sendOrderedBroadcast(intent, null);

                DBManager.save(message);
//                mContext.sendBroadcast(intent);
            }else {
                Intent intent = new Intent();
                switch (msg.what){
                    case WAITING_FOR_CLIENT:
                        //初始化服务器完成
                        intent.setAction(BluetoothMessage.ACTION_INIT_COMPLETE);
                        break;
                    case IS_CONNECTING_SERVER:
                        //正在连接服务器

                        break;
                    case CONNECTED_CLIENT:
                        //有客户端连接到自己

                        break;
                    case CONNECT_CLIENT_ERROR:
                        //连接客户端出错

                        break;
                    case CONNECT_SERVER_ERROR:
                        //连接服务器出错
                        intent.putExtra(EXTRA_ERROR_MSG,(String)msg.obj);
                        intent.setAction(BluetoothMessage.ACTION_CONNECT_ERROR);
                        break;
                    case CONNECTED_SERVER:
                        intent.putExtra(EXTRA_REMOTE_ADDRESS,(String)msg.obj);
                        intent.setAction(BluetoothMessage.ACTION_CONNECTED_SERVER);
                        break;

                }
                mContext.sendBroadcast(intent);
//                String msgContent = (String) msg.obj;
//                Toast.makeText(mContext, msgContent, Toast.LENGTH_SHORT).show();
            }

        }
    };


    private BluetoothUtils(Context context) {
        mContext = context;
        if (context == null) {
            throw new RuntimeException("Parameter context can not be null !");
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothUtils getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothUtils(context);
        }
        return instance;
    }

    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            mContext.startActivity(intent);
            bluetoothAdapter.enable();
        }
    }

    public void scanDevices() {
        if (!bluetoothAdapter.isEnabled()) {
            enableBluetooth();
            return;
        }
        bluetoothAdapter.startDiscovery();
        startService();
    }

    private void startService() {
        Intent serverIntent = new Intent(mContext, ServerService.class);
        mContext.startService(serverIntent);

        Intent clientIntent = new Intent(mContext, MessageService.class);
        mContext.startService(clientIntent);
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public ArrayList<BluetoothDevice> getAvailableDevices() {
        Set<BluetoothDevice> availableDevices = bluetoothAdapter.getBondedDevices();
        ArrayList availableList = new ArrayList();
        for (Iterator<BluetoothDevice> iterator = availableDevices.iterator(); iterator.hasNext(); ) {
            availableList.add(iterator.next());
        }
        return availableList;
    }

    public boolean isBonded(BluetoothDevice device) {
        Set<BluetoothDevice> availableDevices = bluetoothAdapter.getBondedDevices();
        for (Iterator<BluetoothDevice> iterator = availableDevices.iterator(); iterator.hasNext(); ) {
            if (device.getAddress().equals(iterator.next().getAddress())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDiscoverying() {
        return bluetoothAdapter.isDiscovering();
    }

    public void cancelScan() {
        bluetoothAdapter.cancelDiscovery();
    }


    //开启客户端
    private class ClientThread extends Thread {

        private String remoteAddress;

        public ClientThread(String remoteAddress) {
            this.remoteAddress = remoteAddress;
        }

        @Override
        public void run() {
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                // socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(remoteAddress);
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
//                socketMap.put(remoteAddress, socket);
                //连接
                Message msg2 = new Message();
                msg2.obj = "请稍候，正在连接服务器:" + remoteAddress;
                msg2.what = IS_CONNECTING_SERVER;
                linkDetectedHandler.sendMessage(msg2);

                socket.connect();

//                socketMap.put(BluetoothMessage.bluetoothAddress, socket);
                socketMap.put(remoteAddress, socket);

                Message msg = new Message();
//                msg.obj = "已经连接上服务端！可以发送信息。";
                msg.obj = remoteAddress;
                msg.what = CONNECTED_SERVER;
                linkDetectedHandler.sendMessage(msg);
                //启动接受数据
                ReadThread mreadThread = new ReadThread(remoteAddress);
                readThreadMap.put(remoteAddress,mreadThread);
                mreadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                socketMap.remove(remoteAddress);
                Log.e("connect", e.getMessage(), e);
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。"+e.getMessage();
                msg.what = CONNECT_SERVER_ERROR;
                linkDetectedHandler.sendMessage(msg);

//                remoteDeviceMap.remove(remoteAddress);
            }
        }
    }


    //开启服务器
    private class ServerThread extends Thread {
        @Override
        public void run() {

            try {
                    /* 创建一个蓝牙服务器
                     * 参数分别：服务器名称、UUID   */
                mServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM, UUID.fromString(UUIDString));


                while (true){
                    Log.d("server", "wait cilent connect...");

                    Message msg = new Message();
                    msg.obj = "请稍候，正在等待客户端的连接...";
                    msg.what = WAITING_FOR_CLIENT;
                    linkDetectedHandler.sendMessage(msg);

                    /* 接受客户端的连接请求 */
                    BluetoothSocket socket = mServerSocket.accept();
                    socketMap.put(socket.getRemoteDevice().getAddress(), socket);
//                    remoteDeviceMap.put(socket.getRemoteDevice().getAddress(),socket.getRemoteDevice());
                    Log.d("server", "accept success !");

                    Message msg2 = new Message();
                    String info = "客户端已经连接上！可以发送信息。";
                    msg2.obj = info;
                    msg.what = CONNECTED_CLIENT;
                    linkDetectedHandler.sendMessage(msg2);
                    //启动接受数据
                    ReadThread mreadThread = new ReadThread(socket.getRemoteDevice().getAddress());
                    readThreadMap.put(socket.getRemoteDevice().getAddress(),mreadThread);
                    mreadThread.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* 停止服务器 */
    private void shutdownServer() {
        new Thread() {
            @Override
            public void run() {
                try {

                    if (startServerThread != null) {
                        startServerThread.interrupt();
                        startServerThread = null;
                    }
                    Set<String> keySet = socketMap.keySet();
                    for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                        String remoteDeviceAddress = iterator.next();
                        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
                        if (socket != null) {
                            socket.close();
                            socketMap.remove(remoteDeviceAddress);
                        }

                        ReadThread mreadThread = readThreadMap.get(remoteDeviceAddress);
                        if (mreadThread != null) {
                            mreadThread.interrupt();
                            readThreadMap.remove(remoteDeviceAddress);
                        }
                    }

                    if (mServerSocket != null) {
                        mServerSocket.close();/* 关闭服务器 */
                        mServerSocket = null;
                    }
                } catch (IOException e) {
                    Log.e("server", "mServerSocket.close()", e);
                }
            }
        }.start();
    }




    /* 停止客户端连接 */
    private void shutdownClient() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (clientConnectThread != null) {
                        clientConnectThread.interrupt();
                        clientConnectThread = null;
                    }
                    Set<String> keySet = socketMap.keySet();
                    for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                        String remoteDeviceAddress = iterator.next();
                        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
                        if (socket != null) {
                            socket.close();
                            socketMap.remove(remoteDeviceAddress);
                        }
                        ReadThread mreadThread = readThreadMap.get(remoteDeviceAddress);
                        if (mreadThread != null) {
                            mreadThread.interrupt();
                            readThreadMap.remove(remoteDeviceAddress);
                        }

                    }
                }catch (Exception e){
                    Log.d("shutdownCLient", e.getMessage());
                }
            }
        }.start();
    }

    //发送数据
    public void sendMessageHandle(BluetoothMessage msg,String remoteDeviceAddress) {
        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
        if (socket == null) {
            Toast.makeText(mContext, "正在连接...", Toast.LENGTH_SHORT).show();
            connect(remoteDeviceAddress);
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
//            os.write(msg.getBytes());
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(msg);

            msg.setReceiver(remoteDeviceAddress);
            DBManager.save(msg);
        } catch (IOException e) {
            closeConnection(remoteDeviceAddress);
            e.printStackTrace();
        }
    }

    private void closeConnection(String remoteDeviceAddress) {
        socketMap.remove(remoteDeviceAddress);
//        remoteDeviceMap.remove(remoteDeviceAddress);
        readThreadMap.remove(remoteDeviceAddress);
    }

    //读取数据
    private class ReadThread extends Thread {

        private String remoteDeviceAddress;

        public ReadThread(String remoteDeviceAddress){
            this.remoteDeviceAddress = remoteDeviceAddress;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
//                    if ((bytes = mmInStream.read(buffer)) > 0) {
//                        byte[] buf_data = new byte[bytes];
//                        for (int i = 0; i < bytes; i++) {
//                            buf_data[i] = buffer[i];
//                        }
//                        String s = new String(buf_data);
//                        Message msg = new Message();
//                        msg.obj = s;
//                        msg.what = 1;
//                        linkDetectedHandler.sendMessage(msg);
//                    }

                    ObjectInputStream ois = new ObjectInputStream(mmInStream);
                    BluetoothMessage message = (BluetoothMessage) ois.readObject();
                    message.setSender(remoteDeviceAddress);
                    message.setIsMe(0);
                    Message msg = new Message();
                    msg.obj = message;
                    msg.what = 1;
                    linkDetectedHandler.sendMessage(msg);

                } catch (Exception e) {
                    try {
//                        BluetoothMessage.isOpen = false;
                        closeConnection(remoteDeviceAddress);
                        mmInStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private ClientThread clientConnectThread;
    public ServerThread startServerThread;

//    public void init(String remoteAddress, BluetoothMessage.DeviceType deviceType) {
//        BluetoothMessage.deviceType = deviceType;
//        BluetoothMessage.bluetoothAddress = remoteAddress;
//
////        if (BluetoothMessage.isOpen) {
////            Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();
////            return;
////        }
//        if (BluetoothMessage.deviceType == BluetoothMessage.DeviceType.CLIENT) {
//            if (!TextUtils.isEmpty(remoteAddress)) {
//                BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(remoteAddress);
//                if (!remoteDeviceMap.containsKey(remoteAddress)) {
//                    remoteDeviceMap.put(remoteAddress, remoteDevice);
//                    clientConnectThread = new ClientThread(remoteAddress);
//                    clientConnectThread.start();
//                }
////                BluetoothMessage.isOpen = true;
//            } else {
//                Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
//            }
//        } else if (BluetoothMessage.deviceType == BluetoothMessage.DeviceType.SERVER) {
//            startServerThread = new ServerThread();
//            startServerThread.start();
//            BluetoothMessage.isOpen = true;
//        }
//    }

    public void onDestroy() {
//        if (BluetoothMessage.deviceType == BluetoothMessage.DeviceType.CLIENT) {
//            shutdownClient();
//        } else if (BluetoothMessage.deviceType == BluetoothMessage.DeviceType.SERVER) {
//            shutdownServer();
//        }
//        BluetoothMessage.isOpen = false;
//        BluetoothMessage.deviceType = BluetoothMessage.DeviceType.NONE;

        shutdownClient();
        shutdownServer();
    }

    public void connect(String remoteAddress){
        if (!TextUtils.isEmpty(remoteAddress)) {
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(remoteAddress);

            if (!socketMap.containsKey(remoteAddress)) {
//                remoteDeviceMap.put(remoteAddress, remoteDevice);
                if(clientConnectThread!=null && clientConnectThread.isAlive()){
                    return;
                }
                clientConnectThread = new ClientThread(remoteAddress);
                clientConnectThread.start();
            }else {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_REMOTE_ADDRESS,remoteAddress);
                intent.setAction(BluetoothMessage.ACTION_CONNECTED_SERVER);
                mContext.sendBroadcast(intent);
            }
        } else {
            Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
        }
    }

    public void initServer(){
        startServerThread = new ServerThread();
        startServerThread.start();
    }


    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

}
