package com.imaodou.mdlabapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imaodou.mdlabapp.db.MdLabDBHelper;
import com.imaodou.mdlabapp.device.DeviceWeatherStation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  static final String AQI_LIGHTDESC = "空气质量很好，适合出行。";
    private static final String AQI_MEDIUMDESC = "空气质量较差，请减少外出！";
    private static final String AQI_HARDDESC = "空气严重污染，请尽量待在室内！！！";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView weatherName;
    private ImageView weatherIcon;
    private ImageView aqiIcon;
    private TextView aqiDesc;
    private TextView temperature;
    private TextView humidity;
    private TextView pressure;
    private TextView windSpeed;
    private TextView windDirection;
    private TextView rainCollect;
    private TextView sunShine;
    private TextView pm25;
    private TextView uvLight;
    private TextView aqiLevel;

    private static final String TOOLBARNAME = "实时气象";
    private TextView toolBarTitle;

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "DeviceDetailFragment";
    private ReceiveBroadCast receiveBroadCast;
    private DeviceWeatherStation mWeatherMsg;

    public DeviceDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param1 Parameter 2.
     * @return A new instance of fragment DeviceDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceDetailFragment newInstance(String param1) {
        DeviceDetailFragment fragment = new DeviceDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mWeatherMsg = new DeviceWeatherStation();
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.collapsing_toolbar_layout);
            appBarLayout.setTitle(TOOLBARNAME);

            if (appBarLayout != null) {
                if (mParam1.startsWith("MDLab_WeatherStation")) {
                } else {
                    appBarLayout.setTitle(mParam1);
                }

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_device_detail, container, false);
        rootView.setBackgroundResource(R.mipmap.bg_cloudy_night_blur);
        weatherName = (TextView) rootView.findViewById(R.id.weather_name_tv);
        weatherIcon = (ImageView) rootView.findViewById(R.id.details_icon);
        aqiDesc = (TextView) rootView.findViewById(R.id.aqi_desc);
        aqiIcon = (ImageView) rootView.findViewById(R.id.aqi_icon);
        temperature = (TextView) rootView.findViewById(R.id.feelsTemp_tv);
        humidity = (TextView) rootView.findViewById(R.id.humidity_tv);
        pressure = (TextView) rootView.findViewById(R.id.pressure_tv);
        windSpeed = (TextView) rootView.findViewById(R.id.wind_tv);
        windDirection = (TextView) rootView.findViewById(R.id.wind_desc);
        rainCollect = (TextView) rootView.findViewById(R.id.rain_tv);
        sunShine = (TextView) rootView.findViewById(R.id.sun_tv);
        pm25 = (TextView) rootView.findViewById(R.id.pm25);
        aqiLevel = (TextView) rootView.findViewById(R.id.aqi_level);
//        uvLight = (TextView) rootView.findViewById(R.id.deviceUv);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

//
//        temperature.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(getActivity(), ViewPagerChartsActivity.class);
//                startActivity(intent);
//
//            }
//        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
        Log.d(TAG, "onButtonPressed: ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UpMsg");
        getActivity().registerReceiver(receiveBroadCast, filter);
        Log.d(TAG, "onAttach: ");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "onDetach: ");
    }
    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(receiveBroadCast);
        Log.d(TAG, "onDestroyView: desrotyFragment");
        super.onDestroyView();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class ReceiveBroadCast extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
//            textView.setText(intent.getExtras().getString("deviceStates"));
            byte[] showData = intent.getByteArrayExtra("deviceStates");
            //如果解码成功则初始化UI，保存数据到数据库
            if (mWeatherMsg.decodeWeatherStationMsg(showData)) {
                Log.d(TAG, "onReceive: decodemsg success!");
                initView(mWeatherMsg);
                mWeatherMsg.saveWeatherMsgIntoDB();

            }

        }

    }

    void initView(DeviceWeatherStation deviceWeatherStation) {
        Log.d(TAG, "initView: ");
        weatherName.setText("晴天");
        weatherIcon.setImageResource(R.mipmap.ic_sunny_big);
        if (deviceWeatherStation.getPM25() > 300) {
            weatherName.setText("雾霾");
            weatherIcon.setImageResource(R.mipmap.ic_haze_big);
            aqiIcon.setImageResource(R.mipmap.biz_plugin_weather_greater_300);
            aqiLevel.setText("差");
            aqiDesc.setText(AQI_HARDDESC);
        } else if (deviceWeatherStation.getPM25() > 200 ){
            aqiIcon.setImageResource(R.mipmap.biz_plugin_weather_201_300);
            aqiLevel.setText("中");
            aqiDesc.setText(AQI_HARDDESC);
        } else if (deviceWeatherStation.getPM25() > 100) {
            aqiIcon.setImageResource(R.mipmap.biz_plugin_weather_101_150);
            aqiLevel.setText("良");
            aqiDesc.setText(AQI_MEDIUMDESC);
        } else {
            aqiIcon.setImageResource(R.mipmap.biz_plugin_weather_0_50);
            aqiLevel.setText("优");
            aqiDesc.setText(AQI_LIGHTDESC);
        }
        if ((deviceWeatherStation.getPM25() <= 300) && (deviceWeatherStation.getRainCollect() > 0)) {
            weatherName.setText("雨天");
            weatherIcon.setImageResource(R.mipmap.ic_heavyrain_big);
        }
        if ((deviceWeatherStation.getRainCollect() == 0) && (deviceWeatherStation.getSunShine() < 20) && (deviceWeatherStation.getPM25() <= 300)){
            weatherName.setText("阴天");
            weatherIcon.setImageResource(R.mipmap.ic_cloudy_big);
        }

        temperature.setText(Integer.toString(mWeatherMsg.getTemperature()) + " ℃");
        humidity.setText( Integer.toString(mWeatherMsg.getHumidity()) + " %");
        pressure.setText(Float.toString(mWeatherMsg.getPressure()) + " hPa");
        windSpeed.setText(Integer.toString(mWeatherMsg.getWindSpeed()) + " cm/s");
        windDirection.setText((mWeatherMsg.getWindDirection()));
        rainCollect.setText(Integer.toString(mWeatherMsg.getRainCollect()) + " mm");
        sunShine.setText(Integer.toString(mWeatherMsg.getSunLux()) + " lx");
        pm25.setText(Float.toString(mWeatherMsg.getPM25()));
//        uvLight.setText("紫外线：    " + Integer.toString(mWeatherMsg.getUvLight()));

    }
}
