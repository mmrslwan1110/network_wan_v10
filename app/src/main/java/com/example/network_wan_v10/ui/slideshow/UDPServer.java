package com.example.network_wan_v10.ui.slideshow;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class UDPServer {
    /**
     * 端口
     */
    //int port=1888;
    DatagramSocket socket;
    String lastString = (-1 + "");
    int sameTime = 0;
    public handleReceiveData callback;

    private static String a ="192.168.201.130";

    public static String getA() {
        return a;
    }

    public static void setA(String a) {
        UDPServer.a = a;
    }

    public UDPServer(int port) throws SocketException {

        socket = new DatagramSocket(port);  //服务端DatagramSocket
        System.out.println("服务器启动。");
    }

    public void setReceiveCallback(handleReceiveData call) {
        callback = call;
    }

    public void service() throws IOException {
        while (true) {
            DatagramPacket dp = new DatagramPacket(new byte[102400], 102400);
            socket.receive(dp); //接收客户端信息

            byte[] data = dp.getData();
            callback.handleReceive(data);
        }
    }

    public void start() throws SocketException, IOException {
        service();
    }

    public void sendMsg(final byte[] data) {

        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        a=getA();
                        SocketAddress socketAddres = new InetSocketAddress(a, 8805);/*发送端口号*/

                        socket.send(new DatagramPacket(data, data.length, socketAddres));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        send.start();
    }


}