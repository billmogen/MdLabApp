package com.imaodou.mdlabapp.device;

import android.util.Log;

/**
 * Created by billmogen on 2017/3/14.
 */

public class DeviceHome extends Devices {
    private static final String TAG = "DeviceHome";
    public DeviceHome() {
        mHomeLightSwitch = false;
        mHomeFanSwitch = false;
        mHomeBlindSwitch = false;
        mHomeWindowSwitch = false;
        mHomeWarningSwitch = false;
        mHomeTemperature = mHomeHumidity = mHomeSunshine = mHomeUvLight = mHomeWarningVal = 0;
        mHomeRelayState = 0;
    }
    public byte mHomeRelayState;
    public int mHomeTemperature;
    public int mHomeHumidity;
    public int mHomeSunshine;
    public int mHomeUvLight;
    public int mHomeWarningVal;

    public boolean mHomeLightSwitch, mHomeFanSwitch, mHomeBlindSwitch, mHomeWindowSwitch, mHomeWarningSwitch;

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
        mHomeTemperature = tmpData[5] & 0xff;
        if ((mHomeTemperature < 0) || (mHomeTemperature > 51)) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg temperature failed!");
            return false;
        }
        mHomeHumidity = tmpData[6] & 0xff;
        if ((mHomeHumidity < 0) || (mHomeHumidity > 90)) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg humidity failed!");
            return false;
        }

        mHomeUvLight = tmpData[16] & 0xff;
        int tSunLux = 0;
        tSunLux = (tmpData[19] & 0xff);
        tSunLux = (tSunLux << 8) + tmpData[20];
        mHomeSunshine = tSunLux;

        return true;
    }

}
