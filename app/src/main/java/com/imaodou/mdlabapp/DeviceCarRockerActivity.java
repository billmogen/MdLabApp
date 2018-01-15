package com.imaodou.mdlabapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
//import android.widget.Switch;
import android.widget.TextView;

import com.imaodou.mdlabapp.device.DeviceCar;
import com.imaodou.mdlabapp.net.TcpClientConnector;
import com.imaodou.mdlabapp.view.RockerView;
import com.kyleduo.switchbutton.SwitchButton;
import java.io.IOException;

import static com.imaodou.mdlabapp.device.Devices.HOSTIP;
import static com.imaodou.mdlabapp.device.Devices.TCPPORT;
import static com.imaodou.mdlabapp.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static com.imaodou.mdlabapp.view.RockerView.DirectionMode.DIRECTION_4_ROTATE_0;
import static com.imaodou.mdlabapp.view.RockerView.DirectionMode.DIRECTION_4_ROTATE_45;
import static com.imaodou.mdlabapp.view.RockerView.DirectionMode.DIRECTION_8;

public class DeviceCarRockerActivity extends AppCompatActivity {

    private RockerView mRockerViewMotor;
    private TextView mMotorDirection;
    private TextView mMotorSpeed;
    private SwitchButton mSpeaker;
    private ImageButton mBuzzer;
    private RadioGroup mCarMode;
    private SeekBar seekBar;
    private Toolbar mToolBar;
    private TextView mToolBarTitle;
    public static final String TAG = "DeviceCarRockerActivity";
    public static final String BASECMD = "FF03010202020001FF000000FF001100000000000000"; //前进

    private TcpClientConnector tcpClientConnector;
    private DeviceCar smartCar;
    private int updateSpeedFlag;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 设置全屏
        // 屏幕长亮
        setContentView(R.layout.activity_device_car_rocker);
        mToolBar = (Toolbar) findViewById(R.id.toolbar2);
        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolBar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolBarTitle.setText("智能动车");

        tcpClientConnector = TcpClientConnector.getInstance();
        tcpClientConnector.creatConnect(HOSTIP, TCPPORT);
        smartCar = new DeviceCar();
        mRockerViewMotor = (RockerView) findViewById(R.id.my_rocker1);
        mMotorDirection = (TextView) findViewById(R.id.motorDirection);
        mMotorSpeed = (TextView) findViewById(R.id.motorSpeed);
        mCarMode = (RadioGroup) findViewById(R.id.radioGroup);
        mSpeaker = (SwitchButton) findViewById(R.id.speaker);
        mBuzzer = (ImageButton) findViewById(R.id.buzzerButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        RadioButton radioButtonManaual = (RadioButton) findViewById(R.id.modeManual);
        radioButtonManaual.setButtonDrawable(R.drawable.radio);
        RadioButton radioButtonFollow = (RadioButton) findViewById(R.id.modeFollowLine);
        radioButtonFollow.setButtonDrawable(R.drawable.radio);
        RadioButton radioButtonAvoid = (RadioButton) findViewById(R.id.modeAvoid);
        radioButtonAvoid.setButtonDrawable(R.drawable.radio);
        mMotorSpeed.setText("当前速度：100");
        updateSpeedFlag = 0;
        mRockerViewMotor.setOnShakeListener(DIRECTION_4_ROTATE_45, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }
            @Override
            public void direction(RockerView.Direction direction) {
                byte[] sentBuffer = hex2Bytes(BASECMD);
                if (smartCar.carMode == 0) {
                    if (direction == RockerView.Direction.DIRECTION_CENTER){
                        mMotorDirection.setText("当前状态：停止");
                        sentBuffer[7] = 0;
                        smartCar.carState = 0;
                    }else if (direction == RockerView.Direction.DIRECTION_DOWN){
                        mMotorDirection.setText("当前状态：后退");
                        sentBuffer[7] = 4;
                        smartCar.carState = 4;
                    }else if (direction == RockerView.Direction.DIRECTION_LEFT){
                        mMotorDirection.setText("当前状态：左转");
//                        mMotorSpeed.setText("当前速度：255");
                        sentBuffer[7] = 2;
                        smartCar.carState = 2;
                    }else if (direction == RockerView.Direction.DIRECTION_UP){
                        mMotorDirection.setText("当前状态：前进");

                        sentBuffer[7] = 1;
                        smartCar.carState =1;
                    }else if (direction == RockerView.Direction.DIRECTION_RIGHT){
                        mMotorDirection.setText("当前状态：右转");
//                        mMotorSpeed.setText("当前速度：255");
                        sentBuffer[7] = 3;
                        smartCar.carState = 3;
                    }
                    //距离不变的时候，方向改变才能发数据 或者 方向为中间的时候
                    
                    if (smartCar.carLevel >= 9 || smartCar.carState == 0) {
                        Log.d(TAG, "direction: fuckyou");
                        try {
                            if (sentBuffer != null) {
                                tcpClientConnector.send(sentBuffer);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }    
                    }

                }

            }
            @Override
            public void onFinish() {

            }
        });
        mRockerViewMotor.setOnDistanceLevelListener(new RockerView.OnDistanceLevelListener() {
            @Override
            public void onDistanceLevel(int level) {
                smartCar.carLevel = level;
                Log.d(TAG, "onDistanceLevel: " + smartCar.carLevel);
                //方向不变的情况下距离达到最大才能发数据
                if (smartCar.carLevel >= 9 && smartCar.carMode == 0) {
                    byte[] sentBuffer = hex2Bytes(BASECMD);
                    sentBuffer[7] = (byte) smartCar.carState;
                    Log.d(TAG, "onDistanceLevel: fuckyou");
                    try {
                        if (sentBuffer != null) {
                            tcpClientConnector.send(sentBuffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mCarMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.modeManual:
                        smartCar.carMode = 0;
                        break;
                    case R.id.modeFollowLine:
                        smartCar.carMode = 1;
                        mMotorDirection.setText("当前状态：循迹");
                        break;
                    case R.id.modeAvoid:
                        smartCar.carMode = 2;
                        mMotorDirection.setText("当前状态：避障");
                        break;
                    default:
                        break;
                }
                byte[] sentBuffer = hex2Bytes(BASECMD);
                sentBuffer[4] = 2;
                sentBuffer[5] = 2;
                sentBuffer[7] = 0;
                sentBuffer[15] = (byte) smartCar.carMode;
                try {
                    if (sentBuffer != null) {
                        tcpClientConnector.send(sentBuffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mBuzzer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] sentBuffer = hex2Bytes(BASECMD);
                sentBuffer[4] = 0;
                sentBuffer[5] = 1;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "onTouch: down");
                        mBuzzer.setImageResource(R.mipmap.buzzeropen);
                        if (smartCar.speakerState == 0) {
                            sentBuffer[14] = 1; // 开喇叭，关语音
                        } else {
                            sentBuffer[14] = 3; //开喇叭，开语音
                        }
                        smartCar.buzzerState = 1;
                        try {
                            if (sentBuffer != null) {
                                tcpClientConnector.send(sentBuffer);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "onTouch: up");
                        mBuzzer.setImageResource(R.mipmap.buzzerclose);
                        if (smartCar.speakerState == 0) {
                            sentBuffer[14] = 0;  //关喇叭，关语音
                        } else {
                            sentBuffer[14] = 2; //关喇叭，开语音
                        }
                        smartCar.buzzerState = 0;
                        try {
                            if (sentBuffer != null) {
                                tcpClientConnector.send(sentBuffer);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mSpeaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte[] sentBuffer = hex2Bytes(BASECMD);
                sentBuffer[4] = 0;
                sentBuffer[5] = 1;
                if (isChecked) {
                    if (smartCar.buzzerState == 0) {
                        sentBuffer[14] = 2; //开语音 ， 关喇叭
                    } else {
                        sentBuffer[14] = 3; //开语音， 开喇叭
                    }
                    smartCar.speakerState = 1;
                } else {
                    if (smartCar.buzzerState == 1) {
                        sentBuffer[14] = 1; //关语音， 开喇叭
                    } else {
                        sentBuffer[14] = 0; //关语音， 关喇叭
                    }
                    smartCar.speakerState = 0;
                }
                try {
                    if (sentBuffer != null) {
                        tcpClientConnector.send(sentBuffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMotorSpeed.setText("当前速度： " + Integer.toString(progress));
                smartCar.motorSpeed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (smartCar.carMode == 0) {
                    byte[] sentBuffer = hex2Bytes(BASECMD);
                    sentBuffer[4] = 64;
                    sentBuffer[12] = (byte)smartCar.motorSpeed;
                    try {
                        if (sentBuffer != null) {
                            tcpClientConnector.send(sentBuffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

}
