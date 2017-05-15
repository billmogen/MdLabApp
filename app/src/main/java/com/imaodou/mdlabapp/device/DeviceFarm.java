package com.imaodou.mdlabapp.device;

import android.util.Log;

/**
 * Created by billmogen on 2017/3/14.
 */

public class DeviceFarm extends Devices {
    public DeviceFarm() {

        mFarmFanState = false;
        mFarmLightState = false;
        mFarmPumpState = false;
        mRelayState = 0;
        mTemperature = 0;
        mAirHumidity = 0;
        mSoilHumidity = 0;
    }
    private static final String TAG = "DeviceFarm: ";
    public boolean mFarmLightState;
    public boolean mFarmFanState;
    public boolean mFarmPumpState;
    public byte mRelayState;
    public int mTemperature;
    public int mAirHumidity;
    public int mSoilHumidity;

    public boolean decodeFarmMsg(byte[] data) {
        if (data == null) {
            Log.d(TAG, "decodeWeatherStationMsg: data is null! ");
            return false;
        }
        byte[] tmpData = data;
        if (tmpData.length != 22) {
            Log.d(TAG, "decodeWeatherStationMsg: length check faile! " + tmpData.length);
            return false;
        }
        if ((tmpData[0] & 0xff) != 0xff) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg header failed! " + Integer.toHexString(tmpData[0] & 0xff));
            return false;
        }
        if ((tmpData[21] & 0xff) != 0xff) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg tail failed! " + Integer.toHexString(tmpData[21] & 0xff));
            return false;
        }
        mTemperature = tmpData[5] & 0xff;
        if ((mTemperature < 0) || (mTemperature > 51)) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg temperature failed!");
            return false;
        }
        mAirHumidity = tmpData[6] & 0xff;
        if ((mAirHumidity < 0) || (mAirHumidity > 90)) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg humidity failed!");
            return false;
        }
        mSoilHumidity = tmpData[16] & 0xff;

        return true;
    }
}