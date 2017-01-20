package com.imaodou.mdlabapp.device;

import android.util.Log;

/**
 * Created by billmogen on 2017/1/6.
 */

public class DeviceWeatherStation {

    private String  deviceName;
    private int temperature;
    private int humidity;
    private float pressure;
    private int windSpeed;
    private int windDirection;
    private int rainCollect;
    private int sunShine;
    private float pm25;
    private int uvLight;
    private int voltage;
    private int warnVal;
    private int faultVal;

    private static final String TAG = "DeviceWeatherStation";

    public enum deviceStates {
        OFFLINE,
        ONLINE,
        LOCAL,
        TELNET
    }

    public DeviceWeatherStation() {

    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int  getTemperature() { return temperature;}
    public int getHumidity() {return humidity;}
    public float getPressure() {return pressure;}
    public int getWindSpeed() {return  windSpeed;}
    public String getWindDirection() {
        String strWindDirection;
        switch (windDirection) {
            case 0:
                strWindDirection = "东风";
                break;
            case 1:
                strWindDirection = "西风";
                break;
            case 2:
                strWindDirection = "南风";
                break;
            case 3:
                strWindDirection = "北风";
                break;
            case 4:
                strWindDirection = "东南风";
                break;
            case 5:
                strWindDirection = "西南风";
                break;
            case 6:
                strWindDirection = "东北风";
                break;
            case 7:
                strWindDirection = "西北风";
                break;
            default:
                strWindDirection = "";
                break;

        }
        return strWindDirection;
    }
    public int getRainCollect() {return rainCollect;}
    public float getPM25() {return pm25;}
    public int getSunShine() {return sunShine;}
    public int getUvLight() {return uvLight;}
    public int getVoltage() {return voltage;}
    public int getWarnVal() {return warnVal;}
    public int getFalutVal() {return faultVal;}

    public boolean decodeWeatherStationMsg(byte[] data) {
        if (data == null) {
            Log.d(TAG, "decodeWeatherStationMsg: data is null! ");
            return false;
        }
        byte[] tmpData = data;
        if (tmpData.length != 19) {
            Log.d(TAG, "decodeWeatherStationMsg: length check faile! " + tmpData.length);
            return false;
        }
        if ((tmpData[0] & 0xff) != 0xff) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg header failed! " + Integer.toHexString(tmpData[0]&0xff));
            return false;
        }
        voltage = tmpData[4] & 0xff;
        temperature = tmpData[5] & 0xff;
        humidity = tmpData[6] & 0xff;
        int tPressure = 0;
        tPressure = (tmpData[7] & 0xff);
        tPressure = (tPressure << 8) + tmpData[8];
        pressure = ((float)tPressure)/100;
        pressure = (float)(Math.round(pressure*100))/100;
        windSpeed = ((tmpData[9]&0xff) << 8) + (tmpData[10]&0xff);
        windDirection = tmpData[11] & 0xff;
        rainCollect = tmpData[12] & 0xff;
        sunShine = tmpData[13] & 0xff;
        int tPM25 = 0;
        tPM25 = (tmpData[14] & 0xff);
        tPM25 = (tPM25 << 8) + tmpData[15];
        pm25 = ((float)tPM25)/100;
        pm25 = (float)(Math.round(pm25*100))/100;
        uvLight = tmpData[16] & 0xff;
        warnVal = tmpData[17] & 0xff;
        faultVal = tmpData[18] & 0xff;

        return true;
    }


}
