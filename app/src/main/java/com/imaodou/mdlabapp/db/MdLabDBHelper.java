package com.imaodou.mdlabapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by billmogen on 2017/1/19.
 */

public class MdLabDBHelper extends SQLiteOpenHelper {
    public static final String CTEATE_WEATHER_TABLE = "create table weather ("
            + "id integer primary key autoincrement, "
            + "deviceName text, "
            + "temperature integer, "
            + "humidity integer, "
            + "pressure real, "
            + "windSpeed integer, "
            + "windDirection integer, "
            + "rainCollect integer, "
            + "sunShine integer, "
            + "pm25 real, "
            + "uvLight integer, "
            + "voltage integer, "
            + "warning integer, "
            + "faultVal integer, "
            + "updateTime integer)";

    private Context mContext;

    public MdLabDBHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CTEATE_WEATHER_TABLE);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists weather");
        onCreate(db);
    }
}
