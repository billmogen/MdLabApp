package com.imaodou.mdlabapp;

import android.content.Intent;
import android.media.Image;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.imaodou.mdlabapp.device.DeviceRobot;
import com.imaodou.mdlabapp.net.TcpClientConnector;
import com.imaodou.mdlabapp.util.ToolsUtil;

import java.io.IOException;

import static com.imaodou.mdlabapp.device.Devices.HOSTIP;
import static com.imaodou.mdlabapp.device.Devices.TCPPORT;

public class DeviceRobotActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton tForward, tBackWard, tZuocebu, tYoucebu;
    private Button tActTurnLeft, tActTurnRight, tActDunxia, tActZhanqi, tActBaojin, tActSongkai, tActStop;
    private Button tDanceChinaKongfu, tDanceHappyFirst, tDanceHappySecond, tDancePandaKongfu,
            tDanceJixiewu, tDanceAppleFirst, tDanceAppleSecond, tDanceTaijiquan;
    private Button tReset;
    private Toolbar mToolBar;
    private TextView mToolBarTitle;
    private TcpClientConnector tcpClientConnector;
    private DeviceRobot deviceRobot;
    private static final String TAG = "DeviceRobotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                //.penaltyDialog()
                .penaltyLog()
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_robot);
        mToolBar = (Toolbar) findViewById(R.id.robot_toolbar);
        mToolBarTitle = (TextView) findViewById(R.id.robot_toolbar_title);
        setSupportActionBar(mToolBar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolBarTitle.setText("舞蹈机器人");
        tcpClientConnector = TcpClientConnector.getInstance();
        tcpClientConnector.creatConnect(HOSTIP, TCPPORT);
        deviceRobot = (DeviceRobot) new DeviceRobot();
        InitView();
        tForward.setOnClickListener(this);
        tBackWard.setOnClickListener(this);
        tZuocebu.setOnClickListener(this);
        tYoucebu.setOnClickListener(this);
        tActTurnLeft.setOnClickListener(this);
        tActTurnRight.setOnClickListener(this);
        tActDunxia.setOnClickListener(this);
        tActZhanqi.setOnClickListener(this);
        tActBaojin.setOnClickListener(this);
        tActSongkai.setOnClickListener(this);
        tDanceChinaKongfu.setOnClickListener(this);
        tDanceHappyFirst.setOnClickListener(this);
        tDanceHappySecond.setOnClickListener(this);
        tDancePandaKongfu.setOnClickListener(this);
        tDanceJixiewu.setOnClickListener(this);
        tDanceAppleFirst.setOnClickListener(this);
        tDanceAppleSecond.setOnClickListener(this);
        tDanceTaijiquan.setOnClickListener(this);
        tReset.setOnClickListener(this);
        tActStop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String sTmpCmd = "#18GC1\r\n";
        switch (v.getId()) {
            case R.id.robotForward:
                sTmpCmd = deviceRobot.ACTFORWARD;
                break;
            case R.id.robotBackward:
                sTmpCmd = deviceRobot.ACTBACKWARD;
                break;
            case R.id.robotZuocebu:
                sTmpCmd = deviceRobot.ACTZUOCEBU;
                break;
            case R.id.robotYoucebu:
                sTmpCmd = deviceRobot.ACTYOUCEBU;
                break;
            case R.id.turnLeftBtn:
                sTmpCmd = deviceRobot.ACTTURNLEFT;
                break;
            case R.id.turnRightBtn:
                sTmpCmd = deviceRobot.ACTTURNRIGHT;
                break;
            case R.id.dunxiaBtn:
                sTmpCmd = deviceRobot.ACTDUNXIA;
                break;
            case R.id.zhanqiBtn:
                sTmpCmd = deviceRobot.ACTZHANQI;
                break;
            case R.id.baojinBtn:
                sTmpCmd = deviceRobot.ACTBAOJIN;
                break;
            case R.id.songkaiBtn:
                sTmpCmd = deviceRobot.ACTSONGKAI;
                break;
            case R.id.zhongguogongfuBtn:
                sTmpCmd = deviceRobot.DANCECHINAKONGFU;
                break;
            case R.id.huankuaiwushangBtn:
                sTmpCmd = deviceRobot.DANCEHAPPYFIRST;
                break;
            case R.id.huankuaiwuxiaBtn:
                sTmpCmd = deviceRobot.DANCEHAPPYSECOND;
                break;
            case R.id.gongfuxiongmaoBtn:
                sTmpCmd = deviceRobot.DANCEPANDAKONGFU;
                break;
            case R.id.jixiewuBtn:
                sTmpCmd = deviceRobot.DANCEJIXIEWU;
                break;
            case R.id.xiaopingguoshangBtn:
                sTmpCmd = deviceRobot.DANCEAPPLEFIRST;
                break;
            case R.id.xiaopingguoxiaBtn:
                sTmpCmd = deviceRobot.DANCEAPPLESECOND;
                break;
            case R.id.taijiquanBtn:
                sTmpCmd = deviceRobot.DANCETAIJIQUAN;
                break;
            case R.id.resetBtn:
                sTmpCmd = deviceRobot.ROBOTRESET;
                break;
            case R.id.zanting:
                sTmpCmd = deviceRobot.ACTSTOP;
                break;
            default:
                break;
        }
        byte[] sentMsgBuf = sTmpCmd.getBytes();
        Log.d(TAG, "onClick: sentMsgBuf: " + sentMsgBuf);
        try {
            tcpClientConnector.send(sentMsgBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitView() {
        tForward = (ImageButton) findViewById(R.id.robotForward);
        tBackWard = (ImageButton) findViewById(R.id.robotBackward);
        tZuocebu = (ImageButton) findViewById(R.id.robotZuocebu);
        tYoucebu = (ImageButton) findViewById(R.id.robotYoucebu);
        tActTurnLeft = (Button) findViewById(R.id.turnLeftBtn);
        tActTurnRight = (Button) findViewById(R.id.turnRightBtn);
        tActDunxia = (Button) findViewById(R.id.dunxiaBtn);
        tActZhanqi = (Button) findViewById(R.id.zhanqiBtn);
        tActBaojin = (Button) findViewById(R.id.baojinBtn);
        tActSongkai = (Button) findViewById(R.id.songkaiBtn);
        tDanceChinaKongfu = (Button) findViewById(R.id.zhongguogongfuBtn);
        tDanceHappyFirst = (Button) findViewById(R.id.huankuaiwushangBtn);
        tDanceHappySecond = (Button) findViewById(R.id.huankuaiwuxiaBtn);
        tDancePandaKongfu = (Button) findViewById(R.id.gongfuxiongmaoBtn);
        tDanceJixiewu = (Button) findViewById(R.id.jixiewuBtn);
        tDanceAppleFirst = (Button) findViewById(R.id.xiaopingguoshangBtn);
        tDanceAppleSecond = (Button) findViewById(R.id.xiaopingguoxiaBtn);
        tDanceTaijiquan = (Button) findViewById(R.id.taijiquanBtn);
        tReset = (Button) findViewById(R.id.resetBtn);
        tActStop = (Button) findViewById(R.id.zanting);
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
}

