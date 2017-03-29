package com.imaodou.mdlabapp;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.imaodou.mdlabapp.device.DeviceHome;
import com.imaodou.mdlabapp.net.TcpClientConnector;
import com.kyleduo.switchbutton.SwitchButton;

import org.w3c.dom.Text;

import java.io.IOException;

import static com.imaodou.mdlabapp.device.Devices.HOSTIP;
import static com.imaodou.mdlabapp.device.Devices.TCPPORT;

public class DeviceHomeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Toolbar mToolBar;
    private TextView mToolBarTitle;
    private TextView tTemperature;
    private TextView tHumidity;
    private TextView tSunshine;
    private TextView tUVLight;
    private TextView tWaringVal;
    private SwitchButton sLightBtn;
    private SwitchButton sFanBtn;
    private SwitchButton sBlindBtn;
    private SwitchButton sWindowBtn;
    private SwitchButton sWarningBtn;

    private TcpClientConnector tcpClientConnector;
    private DeviceHome deviceHome;
    private static final String TAG = "DeviceHomeActivity";
    private static final String HOMEBASECMD = "FF030102000400000000000000000000000000000000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_home);
        mToolBar = (Toolbar) findViewById(R.id.home_toolbar);
        mToolBarTitle = (TextView) findViewById(R.id.home_toolbar_title);
        setSupportActionBar(mToolBar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolBarTitle.setText("智能家居");
        tcpClientConnector = TcpClientConnector.getInstance();
        tcpClientConnector.creatConnect(HOSTIP, TCPPORT);
        deviceHome = (DeviceHome) new DeviceHome();

        InitView();
        sLightBtn.setOnCheckedChangeListener(this);
        sFanBtn.setOnCheckedChangeListener(this);
        sBlindBtn.setOnCheckedChangeListener(this);
        sWindowBtn.setOnCheckedChangeListener(this);
        sWarningBtn.setOnCheckedChangeListener(this);

        tcpClientConnector.setOnConnectListener(new TcpClientConnector.ConnectListener() {
            @Override
            public void onReceiveData(byte[] data) {
                //do somethings.
                if (deviceHome.decodeFarmMsg(data)) {
                    tTemperature.setText("当前温度：" + deviceHome.mHomeTemperature + "℃");
                    tHumidity.setText("当前湿度：" + deviceHome.mHomeHumidity + "%");
                    tSunshine.setText("光照强度：" + deviceHome.mHomeSunshine + "Lux");
                    tUVLight.setText("紫外线：" + deviceHome.mHomeUvLight);
                } else {
                    String upMsg = byte2hex(data);
                    Log.d(TAG, "onReceiveData: upMsg " + upMsg);
                }

            }
        });
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        byte[] sentMsgBuf = hex2Bytes(HOMEBASECMD);
        switch (buttonView.getId()) {
            case R.id.home_light_switch:
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: openLight");
                    deviceHome.mHomeLightSwitch = true;
                    deviceHome.mHomeRelayState += 2;
                } else {
                    Log.d(TAG, "onCheckedChanged: closeLight");
                    deviceHome.mHomeLightSwitch = false;
                    deviceHome.mHomeRelayState -= 2;
                }
                sentMsgBuf[16] = deviceHome.mHomeRelayState;
                break;
            case R.id.home_fan_switch:
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: openFan");
                    deviceHome.mHomeFanSwitch = true;
                    deviceHome.mHomeRelayState += 4;
                } else {
                    Log.d(TAG, "onCheckedChanged: closeFan");
                    deviceHome.mHomeFanSwitch = false;
                    deviceHome.mHomeRelayState -= 4;
                }
                sentMsgBuf[16] = deviceHome.mHomeRelayState;
                break;
            case R.id.home_blind_switch:
                sentMsgBuf[4] += 2;
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: openBlind");
                    deviceHome.mHomeBlindSwitch = true;
                    //motor forward
                    sentMsgBuf[7] = 0x01;
                } else {
                    Log.d(TAG, "onCheckedChanged: closeBlind");
                    deviceHome.mHomeBlindSwitch = false;
                    //motor backward
                    sentMsgBuf[7] = 0x02;
                }
                break;
            case R.id.home_window_switch:
                sentMsgBuf[4] += 4;
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: openWindow");
                    deviceHome.mHomeWindowSwitch = true;
                    sentMsgBuf[8] = 0x5A;
                } else {
                    Log.d(TAG, "onCheckedChanged: closeWindow");
                    sentMsgBuf[8] = 0x00;
                }
                break;
            case R.id.home_warning_switch:
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: openWarning");
                } else {
                    Log.d(TAG, "onCheckedChanged: closeWarning");
                }
                break;
            default:
                break;
        }
        try {
            tcpClientConnector.send(sentMsgBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void InitView() {
        tTemperature = (TextView) findViewById(R.id.home_temperature);
        tHumidity = (TextView) findViewById(R.id.home_humidity);
        tSunshine = (TextView) findViewById(R.id.home_sunshine);
        tUVLight = (TextView) findViewById(R.id.home_uvlight);

        sLightBtn = (SwitchButton) findViewById(R.id.home_light_switch);
        sFanBtn = (SwitchButton) findViewById(R.id.home_fan_switch);
        sBlindBtn = (SwitchButton) findViewById(R.id.home_blind_switch);
        sWindowBtn = (SwitchButton) findViewById(R.id.home_window_switch);
        sWarningBtn = (SwitchButton) findViewById(R.id.home_warning_switch);
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
