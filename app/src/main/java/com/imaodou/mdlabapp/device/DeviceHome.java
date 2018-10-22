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
        mHomeJiashiqiSwitch = false;
        mHomeTemperature = mHomeHumidity = mHomeSunshine = mHomeUvLight = mHomeWarningVal = 0;
        mHomeRelayState = 0;
        mHomeWarningStr = "无";
        mHomePm25 = 0;
        mHomeArofene = 0;
    }
    public byte mHomeRelayState;
    public int mHomeTemperature;
    public int mHomeHumidity;
    public int mHomeSunshine;
    public int mHomeUvLight;
    public int mHomeWarningVal;
    public String  mHomeWarningStr;
    public float mHomePm25;
    public float mHomeArofene;

    public boolean mHomeLightSwitch, mHomeFanSwitch, mHomeBlindSwitch, mHomeWindowSwitch, mHomeWarningSwitch, mHomeJiashiqiSwitch;

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
        int tAroVal = (tmpData[7] & 0xff);
        tAroVal = (tAroVal << 8) + tmpData[8];
        mHomeArofene = ((float) tAroVal)/100;
        int tPM25 = (tmpData[14] & 0xff);
        tPM25 = (tPM25 << 8) + tmpData[15];
        mHomePm25 = ((float)tPM25)/100;
        mHomePm25 = (float)(Math.round(mHomePm25*100))/100;
        if (mHomePm25 < 0) {
            mHomePm25 = 0;
        }

        mHomeWarningVal = tmpData[17] & 0xff;
        switch (mHomeWarningVal) {
            case 0:
                mHomeWarningStr = "无";
                break;
            case 1:
                mHomeWarningStr = "烟雾";
                break;
            case 2:
                mHomeWarningStr = "有人";
                break;
            case 3:
                mHomeWarningStr = "烟雾 + 有人";
                break;
            case 4:
                mHomeWarningStr = "门未关闭";
                break;
            case 5:
                mHomeWarningStr = "烟雾 + 门未关闭";
                break;
            default:
                mHomeWarningStr = "无";
                break;
        }


        return true;
    }

}
