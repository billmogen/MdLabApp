package com.imaodou.mdlabapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.imaodou.mdlabapp.util.MyApplication;

/**
 * Created by billmogen on 2017/1/19.
 */

public class MdLabDBHelper extends SQLiteOpenHelper {
    public static final String CTEATE_WEATHER_TABLE = "create table Weather ("
            + "id integer primary key autoincrement, "
            + "deviceName text, "
            + "temperature integer, "
            + "humidity integer, "
            + "pressure real, "
            + "windSpeed integer, "
            + "windDirection integer, "
            + "rainCollect integer, "
            + "sunLux integer, "
            + "pm25 real, "
            + "uvLight integer, "
            + "voltage integer, "
            + "warning integer, "
            + "faultVal integer, "
            + "updateTime integer)";

    private Context mContext;
    private static MdLabDBHelper mInstance = null;
    private static final String DATABASE_NAME = "MdLabStore.db";
    private static final String DATABASE_TABLE = "table_name";
    private static final int DATABASE_VERSION =1;

    public synchronized  static MdLabDBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new MdLabDBHelper(ctx);
        }
        return mInstance;
    }
    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private MdLabDBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CTEATE_WEATHER_TABLE);
        Toast.makeText(MyApplication.getContextObject(), "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists Weather");
        onCreate(db);
    }

    public Cursor selectAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query("Weather", null, null, null, null, null, null);

        return cursor;
    }
    //日期格式： yyyy-MM-dd HH:mm:ss
    public Cursor selectSensorInfo(String tableName, String column, long startTime, long endTime) {

        SQLiteDatabase db = this.getReadableDatabase();
        String sqlStr = "select " + column + " from " + tableName + " where " + "updateTime>=? and updateTime<?";
//        Cursor cursor = db.rawQuery("select temperature from Weather where " + "updateTime>=? and updateTime<?", new String[]{String.valueOf(startTime), String.valueOf(endTime)});
        Cursor cursor = db.rawQuery(sqlStr, new String[]{String.valueOf(startTime), String.valueOf(endTime)});

        return cursor;
    }

    public SQLiteDatabase getDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db;
    }
}
