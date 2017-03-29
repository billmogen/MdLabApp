package com.imaodou.mdlabapp.net;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.imaodou.mdlabapp.util.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by hadisi on 2016/6/28.
 */

public class TcpClientConnector {
    private static TcpClientConnector mTcpClientConnector;
    private Socket mClient;
    private ConnectListener mListener;
    private Thread mConnectThread;
    private static final String TAG = "TcpClientConnector";


    public interface ConnectListener {
        void onReceiveData(byte[] data);
    }

    public void setOnConnectListener(ConnectListener listener) {
        this.mListener = listener;
    }

    public static TcpClientConnector getInstance() {
        if (mTcpClientConnector == null)
            mTcpClientConnector = new TcpClientConnector();
        return mTcpClientConnector;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if (mListener != null) {
                        mListener.onReceiveData(msg.getData().getByteArray("data"));
                    }
                    break;
            }
        }
    };

    public void creatConnect(final String mSerIP, final int mSerPort) {
        if (mConnectThread == null) {
            Log.d(TAG, "creatConnect: mConnectThread is null!");
            mConnectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        connect(mSerIP, mSerPort);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mConnectThread.start();
            Log.d(TAG, "creatConnect: " + mConnectThread.getId());

        } else {

            Log.d(TAG, "creatConnect: mConnectThread not null!");

        }
    }

    /**
     * 与服务端进行连接
     *
     * @throws IOException
     */
    private void connect(String mSerIP, int mSerPort) throws IOException {
        if (mClient == null) {
            mClient = new Socket(mSerIP, mSerPort);
//            mClient.setSoTimeout(5000);
//            mClient.setTcpNoDelay(true);
//            mClient.setReceiveBufferSize(1024);
//            mClient.setSendBufferSize(1024);
            mClient.setKeepAlive(true);
        }
        InputStream inputStream = mClient.getInputStream();
        int count = 22;
        int readCount = 0;
        int len;

        while (inputStream.available() != -1) {
            byte[] buffer = new byte[count];
            byte[] tBuf = new byte[count];
            while(readCount < count) {
                readCount += inputStream.read(buffer, readCount, count - readCount);
                Log.d(TAG, "connect: readCount " + readCount);
            }
            String str = byte2hex(buffer);
            if (str.startsWith("FF")) {
                Message message = new Message();
                message.what = 100;
                Bundle bundle = new Bundle();
                bundle.putByteArray("data", buffer);
                message.setData(bundle);
                mHandler.sendMessage(message);

            } else {
                len = inputStream.read(tBuf);
                Log.d(TAG, "connect: len " + len);
            }
            readCount = 0;
        }

    }

    /**
     * 发送数据
     *
     * @param buffer 需要发送的内容
     */
    public void send(byte[] buffer) throws IOException {
        if (mClient == null) {
            Toast.makeText(MyApplication.getContextObject(), "网络错误，请重新连接！", Toast.LENGTH_SHORT).show();
        } else {
            OutputStream outputStream = mClient.getOutputStream();
            if (outputStream != null) {
                outputStream.write(buffer);
                outputStream.flush();
            }
        }


    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (mClient != null) {
            mClient.close();
            mClient = null;
            mConnectThread.interrupt();
            mConnectThread = null;
        }
    }

    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
}