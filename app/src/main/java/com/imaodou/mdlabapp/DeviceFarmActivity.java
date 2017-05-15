package com.imaodou.mdlabapp;

import android.content.Intent;
import android.media.Image;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaodou.mdlabapp.device.DeviceFarm;
import com.imaodou.mdlabapp.net.TcpClientConnector;
import com.imaodou.mdlabapp.util.ToolsUtil.*;

import java.io.IOException;

import static com.imaodou.mdlabapp.device.Devices.HOSTIP;
import static com.imaodou.mdlabapp.device.Devices.TCPPORT;


public class DeviceFarmActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private TextView mToolBarTitle;
    private TextView mFarmTemperature;
    private TextView mFarmSoilHumidity;
    private TextView mFarmAirHumidity;
    private ImageView mFarmFanImg;
    private ImageView mFarmLightImg;
    private ImageView mFarmPumpImg;
    private ImageButton mFarmFanBtn;
    private ImageButton mFarmLightBtn;
    private ImageButton mFarmPumpBtn;

    private TcpClientConnector tcpClientConnector;
    private DeviceFarm deviceFarm;
    private static final String FARMBASECMD = "FF030102000400000000000000000000000000000000";
    private static final String TAG = "DeviceFarmActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_farm);

        mToolBar = (Toolbar) findViewById(R.id.farm_toolbar);
        mToolBarTitle = (TextView) findViewById(R.id.farm_toolbar_title);
        setSupportActionBar(mToolBar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolBarTitle.setText("智慧农业");
        InitView();
        tcpClientConnector = TcpClientConnector.getInstance();
        tcpClientConnector.creatConnect(HOSTIP, TCPPORT);
        deviceFarm = (DeviceFarm) new DeviceFarm();


        mFarmLightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] sentBuffer =  hex2Bytes(FARMBASECMD);
                if (deviceFarm.mFarmLightState) {
                    deviceFarm.mRelayState -= 2;
                    mFarmLightBtn.setImageResource(R.mipmap.plug_small_off);
                    mFarmLightImg.setImageResource(R.mipmap.device_light_off);
                    deviceFarm.mFarmLightState = false;
                } else {
                    deviceFarm.mRelayState += 2;
                    mFarmLightBtn.setImageResource(R.mipmap.plug_small_on);
                    mFarmLightImg.setImageResource(R.mipmap.device_light_on);
                    deviceFarm.mFarmLightState = true;
                }
                sentBuffer[16] = deviceFarm.mRelayState;
                try {
                    tcpClientConnector.send(sentBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mFarmFanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] sentBuffer =  hex2Bytes(FARMBASECMD);
                if (deviceFarm.mFarmFanState) {
                    deviceFarm.mRelayState -= 4;
                    mFarmFanImg.setImageResource(R.mipmap.device_fan_off);
                    mFarmFanBtn.setImageResource(R.mipmap.plug_small_off);
                    deviceFarm.mFarmFanState = false;
                } else {
                    deviceFarm.mRelayState += 4;
                    mFarmFanImg.setImageResource(R.mipmap.device_fan_on);
                    mFarmFanBtn.setImageResource(R.mipmap.plug_small_on);
                    deviceFarm.mFarmFanState = true;
                }
                sentBuffer[16] = deviceFarm.mRelayState;
                try {
                    tcpClientConnector.send(sentBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mFarmPumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] sentBuffer =  hex2Bytes(FARMBASECMD);
                if (deviceFarm.mFarmPumpState) {
                    deviceFarm.mRelayState -= 1;
                    mFarmPumpBtn.setImageResource(R.mipmap.plug_small_off);
                    mFarmPumpImg.setImageResource(R.mipmap.device_pump_off);
                    deviceFarm.mFarmPumpState = false;
                } else {
                    deviceFarm.mRelayState += 1;
                    mFarmPumpImg.setImageResource(R.mipmap.device_pump_on);
                    mFarmPumpBtn.setImageResource(R.mipmap.plug_small_on);
                    deviceFarm.mFarmPumpState = true;
                }
                sentBuffer[16] = deviceFarm.mRelayState;
                try {
                    tcpClientConnector.send(sentBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tcpClientConnector.setOnConnectListener(new TcpClientConnector.ConnectListener() {
            @Override
            public void onReceiveData(byte[] data) {
                //do somethings.
                if (deviceFarm.decodeFarmMsg(data)) {
                    mFarmTemperature.setText("当前温度：" + deviceFarm.mTemperature + "℃");
                    mFarmAirHumidity.setText("空气湿度：" + deviceFarm.mAirHumidity + "%");
                    mFarmSoilHumidity.setText("土壤湿度：" + deviceFarm.mSoilHumidity + "%");
                } else {
                    String upMsg = byte2hex(data);
                    Log.d(TAG, "onReceiveData: upMsg " + upMsg);
                }

            }
        });
    }

    private void InitView() {
        mFarmTemperature = (TextView) findViewById(R.id.farm_temperature);
        mFarmAirHumidity = (TextView) findViewById(R.id.farm_air_humidity);
        mFarmSoilHumidity = (TextView) findViewById(R.id.farm_soil_humidity);
        mFarmFanImg = (ImageView) findViewById(R.id.farm_fan_img);
        mFarmLightImg = (ImageView) findViewById(R.id.farm_light_img);
        mFarmPumpImg = (ImageView) findViewById(R.id.farm_pump_img);
        mFarmFanBtn = (ImageButton) findViewById(R.id.farm_fan_btn);
        mFarmLightBtn = (ImageButton) findViewById(R.id.farm_light_btn);
        mFarmPumpBtn = (ImageButton) findViewById(R.id.farm_pump_btn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            Log.d(TAG, "onOptionsItemSelected: ");
            NavUtils.navigateUpTo(this, new Intent(this, MdLabMainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            tcpClientConnector.disconnect();

            Log.d(TAG, "onDestroy: tcpClient disconnect ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onDestroy: destoryDeviceAct");
    }
    private static byte[] hex2Bytes(String src){
        byte[] res = new byte[src.length()/2];
        char[] chs = src.toCharArray();
        for(int i=0,c=0; i<chs.length; i+=2,c++){
            res[c] = (byte) (Integer.parseInt(new String(chs,i,2), 16));
        }

        return res;
    }
    private static final String byte2hex(byte b[]) {
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
