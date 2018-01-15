package com.imaodou.mdlabapp.device;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.imaodou.mdlabapp.db.MdLabDBHelper;
import com.imaodou.mdlabapp.util.MyApplication;
import com.imaodou.mdlabapp.util.ToolsUtil;

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
    private int sunLux;

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
    public int getSunLux() {return  sunLux;}

    public boolean decodeWeatherStationMsg(byte[] dataB) {
        if (dataB == null) {
            Log.d(TAG, "decodeWeatherStationMsg: data is null! ");
            return false;
        }
        //dataB[20] = (byte)150;
        int[] tmpData = ToolsUtil.ubyteArr2IntArr(dataB);
        if (tmpData.length != 22) {
            Log.d(TAG, "decodeWeatherStationMsg: length check faile! " + tmpData.length);
            return false;
        }
        if ((tmpData[0] & 0xff) != 0xff) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg header failed! " + Integer.toHexString(tmpData[0]&0xff));
            return false;
        }
        if ((tmpData[21] & 0xff) != 0xff) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg tail failed! " + Integer.toHexString(tmpData[21]&0xff));
            return false;
        }
        voltage = tmpData[4] & 0xff;
        temperature = tmpData[5] & 0xff;
        if ((temperature < 0)) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg temperature failed!");
            return false;
        }
        humidity = tmpData[6] & 0xff;
        if ((humidity < 0)) {
            Log.d(TAG, "decodeWeatherStationMsg: check msg humidity failed!");
            return false;
            
        }
        int tPressure = 0;
        tPressure = (tmpData[7] & 0xff);
        tPressure = (tPressure << 8) + tmpData[8];
        pressure = ((float)tPressure)/100;
        pressure = (float)(Math.round(pressure*100))/100;
        if ((pressure < 0) || (pressure > 140)) {
            return false;
        }
        windSpeed = ((tmpData[9]&0xff) << 8) + (tmpData[10]&0xff);

        windDirection = tmpData[11] & 0xff;
        if ((windDirection < 0) || (windDirection > 7)) {
            return false;
        }
        rainCollect = tmpData[12] & 0xff;
        sunShine = tmpData[13] & 0xff;
        if ((sunShine < 0) || (sunShine > 100)) {
            return false;
        }
        int tPM25 = 0;
        tPM25 = (tmpData[14] & 0xff);
        tPM25 = (tPM25 << 8) + tmpData[15];
        pm25 = ((float)tPM25)/100;
        pm25 = (float)(Math.round(pm25*100))/100;
        uvLight = tmpData[16] & 0xff;
        warnVal = tmpData[17] & 0xff;
        faultVal = tmpData[18] & 0xff;
        int tSunLux = 0;
        tSunLux = (tmpData[19] & 0xff);
        tSunLux = (tSunLux << 8) + tmpData[20];
        sunLux = tSunLux;
        Log.d(TAG, "decodeWeatherStationMsg: sunLux:" + sunLux);

        return true;
    }
    public void saveWeatherMsgIntoDB() {

        MdLabDBHelper mdLabDBHelper = MdLabDBHelper.getInstance(MyApplication.getContextObject());
        SQLiteDatabase db =  mdLabDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deviceName", deviceName);
        values.put("temperature", temperature);
        values.put("humidity", humidity);
        values.put("pressure", pressure);
        values.put("windSpeed", windSpeed);
        values.put("windDirection", windDirection);
        values.put("rainCollect", rainCollect);
        values.put("sunLux", sunLux);
        values.put("pm25", pm25);
        values.put("uvLight", uvLight);
        values.put("voltage", voltage);
        values.put("warning", warnVal);
        values.put("faultVal", faultVal);
        java.util.Date date = new java.util.Date();
        long datetime = date.getTime();
        values.put("updateTime", datetime);
        db.insert("Weather", null, values);
        db.close();

    }

//    "deviceName text, "
//            + "temperature integer, "
//            + "humidity integer, "
//            + "pressure real, "
//            + "windSpeed integer, "
//            + "windDirection integer, "
//            + "rainCollect integer, "
//            + "sunShine integer, "
//            + "pm25 real, "
//            + "uvLight integer, "
//            + "voltage integer, "
//            + "warning integer, "
//            + "faultVal integer, "
//            + "updateTime integer)";
//private String  deviceName;
//    private int temperature;
//    private int humidity;
//    private float pressure;
//    private int windSpeed;
//    private int windDirection;
//    private int rainCollect;
//    private int sunShine;
//    private float pm25;
//    private int uvLight;
//    private int voltage;
//    private int warnVal;
//    private int faultVal;
}
