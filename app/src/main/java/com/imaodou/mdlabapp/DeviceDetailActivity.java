package com.imaodou.mdlabapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.imaodou.mdlabapp.db.MdLabDBHelper;
import com.imaodou.mdlabapp.net.TcpClientConnector;
import com.imaodou.mdlabapp.util.MyApplication;

import java.io.IOException;

import static com.imaodou.mdlabapp.device.Devices.HOSTIP;
import static com.imaodou.mdlabapp.device.Devices.TCPPORT;

public class DeviceDetailActivity extends AppCompatActivity implements DeviceDetailFragment.OnFragmentInteractionListener{

    private TcpClientConnector tcpClientConnector;
    private static final String TAG = "DeviceDetailAct";
    private static final String GETSTATECMD = "FFFF03010201";
    private static final byte[] getCmd = {127,127, 3, 1 ,2,1};

    private MdLabDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle("CollapsingToolbarLayout");
        //通过CollapsingToolbarLayout修改字体颜色
//        mCollapsingToolbarLayout.setExpandedTitleGravity(left| bottom);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.GREEN);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色

        tcpClientConnector = TcpClientConnector.getInstance();
        tcpClientConnector.creatConnect(HOSTIP, TCPPORT);
        Log.d(TAG, "onCreate: tcpClientConnector create!");
        Log.d(TAG, "onCreate: " + tcpClientConnector.toString());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    tcpClientConnector.send(hex2Bytes(GETSTATECMD));
                    Log.d(TAG, "onClick: sent get statue commond");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Log.d(TAG, "onCreate: savedInstanceState null");
            Bundle arguments = new Bundle();
            arguments.putString(DeviceDetailFragment.ARG_PARAM1,
                    getIntent().getStringExtra(DeviceDetailFragment.ARG_PARAM1));
            DeviceDetailFragment fragment = new DeviceDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.device_detail_container, fragment)
                    .commit();
        } else {
            Log.d(TAG, "onCreate: savedInstanceState not null!");
        }

        tcpClientConnector.setOnConnectListener(new TcpClientConnector.ConnectListener() {
            @Override
            public void onReceiveData(byte[] data) {
                //do somethings.
                String upMsg = byte2hex(data);
                Log.d(TAG, "onReceiveData: upMsg " + upMsg);
                Intent intent = new Intent();
                intent.setAction("UpMsg");
                intent.putExtra("deviceStates", data);

                sendBroadcast(intent);
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
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"sent msg to fragment",Toast.LENGTH_LONG).show();
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
    public static byte[] hex2Bytes(String src){
        byte[] res = new byte[src.length()/2];
        char[] chs = src.toCharArray();
        for(int i=0,c=0; i<chs.length; i+=2,c++){
            res[c] = (byte) (Integer.parseInt(new String(chs,i,2), 16));
        }

        return res;
    }
}

