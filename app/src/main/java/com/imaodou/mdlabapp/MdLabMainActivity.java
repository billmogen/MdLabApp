package com.imaodou.mdlabapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imaodou.mdlabapp.db.MdLabDBHelper;
import com.imaodou.mdlabapp.device.DeviceFarm;
import com.imaodou.mdlabapp.device.DeviceWeatherStation;
import com.imaodou.mdlabapp.device.Devices;
import com.imaodou.mdlabapp.net.TcpClientConnector;
import com.imaodou.mdlabapp.net.WifiAdmin;
import com.imaodou.mdlabapp.util.SnackbarUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MdLabMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DeviceDetailFragment.OnFragmentInteractionListener {

    private boolean mTwoPane;
    private WifiManager wifiManager;
    List<ScanResult> scanResults;
    private WifiInfo wifiInfo;
    private WifiAdmin wifiAdmin;
    private final String TAG = "MdLab";
    private boolean mConnectFlag;
    private ConnectionChangeReceiver myReceiver;
    private TcpClientConnector tcpClientConn;
    private RecyclerView recyclerView;
    private Devices devices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md_lab_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tcpClientConn = TcpClientConnector.getInstance();
        devices = new Devices();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        registerReceiver();
        init();
        if (findViewById(R.id.device_detail_container) != null) {
            mTwoPane = true;
        }
        if (mTwoPane) {

            tcpClientConn.setOnConnectListener(new TcpClientConnector.ConnectListener() {
                @Override
                public void onReceiveData(byte[] data) {
                    //do somethings.
                    //String upMsg = data;
                    //Log.d(TAG, "onReceiveData: upMsg " + upMsg);
                }
            });
        }
    }

    private void init() {
        wifiAdmin = new WifiAdmin(MdLabMainActivity.this);
        wifiAdmin.openWifi();
        wifiAdmin.startScan();
        scanResults = wifiAdmin.getWifiList();

         recyclerView = (RecyclerView)findViewById(R.id.device_list);

        if (scanResults == null) {
            //Toast.makeText(this, "open wifi first！", Toast.LENGTH_LONG).show();
            Snackbar.make(recyclerView, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            List<String> ssids = wifiAdmin.getScanWifiResult();
            Iterator<String> ssidsIter = ssids.iterator();
            while (ssidsIter.hasNext()) {
                if (ssidsIter.next().toString().startsWith("MDLab")) {
                } else {
                    ssidsIter.remove();
                }
            }

            assert recyclerView != null;
            recyclerView.setAdapter(new DeviceRecyclerViewAdapter(ssids));

        }
    }

    private void refreshDeviceList() {
        init();
        Log.d(TAG, "refreshDeviceList: refresh ui");
    }
//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new DeviceRecyclerViewAdapter(scanResults));
//    }

    public class DeviceRecyclerViewAdapter
            extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.MyViewHolder> {

        private final List<String> mListWifi;
        //private final WifiInfo mWifiInfo;
        public DeviceRecyclerViewAdapter(List<String> items) {
            mListWifi = items;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.device_list_content, parent, false);
            return new MyViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
           // holder.mDeviceItem = mListWifi.get(position).SSID.toString();
//            ScanResult tScanResult = mListWifi.get(position);
            holder.cardView.setRadius(8);
            holder.cardView.setCardElevation(8);
            holder.cardView.setContentPadding(5,5,5,5);
            String [] tmp = mListWifi.get(position).toString().split("_");
            String str = null;
            Log.d(TAG, "onBindViewHolder: wifiInfo: " + wifiAdmin.getSSID());
            if (wifiAdmin.getSSID().contains(tmp[1] + '_' + tmp[2])) {   //设备在线模式
                holder.mConnectState.setText(R.string.mdlab_device_connect);
                holder.mConnectState.setTextColor(ContextCompat.getColor(MdLabMainActivity.this, R.color.connect_state_text_online));
                holder.mConnectState.setBackgroundColor(ContextCompat.getColor(MdLabMainActivity.this, R.color.connect_state_bg_online));
                switch (tmp[1]) {
                    case "WeatherStation":
                        str = String.format(getString(R.string.mdlab_device_weatherstation));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.weatherstationonline);
                        devices.deviceType = "weatherstation";
                        devices.deviceState = 1;
                        break;
                    case "Car":
                        str = String.format(getString(R.string.mdlab_device_smartcar));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smartcaronline);
                        devices.deviceType = "car";
                        devices.deviceState = 1;
                        break;
                    case "Home":
                        str = String.format(getString(R.string.mdlab_device_smarthome));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smarthomeonline);
                        devices.deviceType = "home";
                        devices.deviceState = 1;
                        break;
                    case "Farm":
                        str = String.format(getString(R.string.mdlab_device_smartfarmer));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smartfarmonline);
                        devices.deviceType = "farm";
                        devices.deviceState = 1;
                        break;
                    case "Robot":
                        str = String.format(getString(R.string.mdlab_device_robot));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smartfarmonline);
                        devices.deviceType = "robot";
                        devices.deviceState = 1;
                        break;
                    default:
                        break;
                }
            } else {
                holder.mConnectState.setText(R.string.mdlab_device_disconnect);
                holder.mConnectState.setTextColor(ContextCompat.getColor(MdLabMainActivity.this, R.color.connect_state_text_offline));
                holder.mConnectState.setBackgroundColor(ContextCompat.getColor(MdLabMainActivity.this, R.color.connect_state_bg_offline));
                switch (tmp[1]) {
                    case "WeatherStation":
                        str = String.format(getString(R.string.mdlab_device_weatherstation));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.weatherstationoffline);
                        break;
                    case "Car":
                        str = String.format(getString(R.string.mdlab_device_smartcar));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smartcaroffline);
                        break;
                    case "Home":
                        str = String.format(getString(R.string.mdlab_device_smarthome));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smarthomeoffline);
                        break;
                    case "Farm":
                        str = String.format(getString(R.string.mdlab_device_smartfarmer));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smartfarmoffline);
                        break;
                    case "Robot":
                        str = String.format(getString(R.string.mdlab_device_robot));
                        str = str + tmp[2].toString();
                        holder.mDeviceName.setText(str);
                        holder.mDeviceImage.setImageResource(R.mipmap.smartfarmoffline);
                        break;
                    default:
                        break;
                }

            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(DeviceDetailFragment.ARG_PARAM1, holder.mDeviceName);
//                        DeviceDetailFragment fragment = new DeviceDetailFragment();
//                        fragment.setArguments(arguments);
                        DeviceDetailFragment fragment = DeviceDetailFragment.newInstance(mListWifi.get(position).toString());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.device_detail_container, fragment)
                                .commit();
                    } else {
                        if (holder.mConnectState.getText().toString().contains(getString(R.string.mdlab_device_disconnect))) {
//                            SnackbarUtil.ShortSnackbar(holder.mView, "请切换到对应wifi", SnackbarUtil.Warning).show();
                            Snackbar snackbar= SnackbarUtil.ShortSnackbar(holder.mView,"请切换到对应wifi",SnackbarUtil.Warning);
                            SnackbarUtil.SnackbarAddView(snackbar,R.layout.snackbar_addview,0);
                            snackbar.show();
                        } else {
                            Context context = v.getContext();
                            if (devices.deviceType.equals("weatherstation")) {
                                Intent intent = new Intent(context, DeviceDetailActivity.class);
                                Log.d(TAG, "onClick: DeviceName " + mListWifi.get(position).toString());
                                intent.putExtra(DeviceDetailFragment.ARG_PARAM1, mListWifi.get(position).toString());
                                context.startActivity(intent);
                            } else if (devices.deviceType.equals("car")) {
                                Intent intent = new Intent(context, DeviceCarRockerActivity.class);
                                context.startActivity(intent);
                            } else if (devices.deviceType.equals("farm")) {
                                Intent intent = new Intent(context, DeviceFarmActivity.class);
                                context.startActivity(intent);
                            } else if (devices.deviceType.equals("home")) {
                                Intent intent = new Intent(context, DeviceHomeActivity.class);
                                context.startActivity(intent);
                            } else if (devices.deviceType.equals("robot")) {
                                Intent intent = new Intent(context, DeviceRobotActivity.class);
                                context.startActivity(intent);
                            }

                        }


                    }
                }
            });
//            holder.mConnectState.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    String tState = holder.mConnectState.getText().toString();
//                    if (mConnectFlag) {
//
//                        //WifiConfiguration wifiConfiguration = wifiAdmin.createWifiInfo(tScanResult.SSID, "12345678", 1);
//                        //wifiAdmin.addNetwork(wifiConfiguration);
//                        //tcpClientConn.creatConnect("192.168.4.22", 1025);
////                        holder.mConnectState.setText("disconnect");
////                        holder.mDeviceImage.setImageResource(R.mipmap.weatherstationonline);
//
//                    } else {
//                        try {
//                            tcpClientConn.disconnect();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        wifiAdmin.disConnectionWifi(wifiAdmin.getNetWordId());
////                        holder.mConnectState.setText("connect");
////                        holder.mDeviceImage.setImageResource(R.mipmap.weatherstationoffline);
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mListWifi.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mDeviceImage;
            public final TextView mDeviceName;
            public Button mConnectState;
            public DeviceWeatherStation mDeviceItem;
            private CardView cardView;

            public MyViewHolder(View view) {
                super(view);
                mView = view;
                cardView = (CardView) view.findViewById(R.id.cardView);
                mDeviceImage = (ImageView) view.findViewById(R.id.deviceImage);
                mDeviceName = (TextView) view.findViewById(R.id.deviceName);
                mConnectState = (Button) view.findViewById(R.id.connectionState);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDeviceName.getText() + "'";
            }
        }
    }

    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //NetworkInfo  mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo  wifiNetInfo=connectivityManager.getActiveNetworkInfo();

            if (wifiNetInfo != null) {
                if (wifiNetInfo.isConnected()) {
                    if (wifiNetInfo.getType() == connectivityManager.TYPE_WIFI) {
                        mConnectFlag = true;
                        Log.d(TAG, "onReceive: wifiConnected!");
                        if (mTwoPane) {
                            tcpClientConn.creatConnect("192.168.4.1", 2001);
                            Log.d(TAG, "onReceive: tcpclientconn create!");
                        }

                    }

                    //改变背景或者 处理网络的全局变量
                } else {
                    mConnectFlag = false;
                }
            } else {
                //改变背景或者 处理网络的全局变量
                mConnectFlag = false;
                //tcpClientConn.disconnect();
            }

        }
    }

    private  void registerReceiver(){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver=new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);
    }

    private  void unregisterReceiver(){
        this.unregisterReceiver(myReceiver);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.md_lab_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_report_image) {
            startActivity(new Intent(this, ViewPagerChartsActivity.class));
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"sent msg to fragment",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            tcpClientConn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unregisterReceiver();
    }

}


