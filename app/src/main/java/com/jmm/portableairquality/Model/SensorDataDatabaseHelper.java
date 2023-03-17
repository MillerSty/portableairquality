package com.jmm.portableairquality.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;

public class SensorDataDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sensor_data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "sensor_data";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_HUMIDITY = "humidity";
    private static final String COLUMN_PM1 = "pm1";
    private static final String COLUMN_PM2 = "pm2";
    private static final String COLUMN_PM10 = "pm10";
    private static final String COLUMN_NOX = "nox";
    private static final String COLUMN_CO = "co";
    private static final String COLUMN_VOC = "voc";
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_TIMESTAMP + " INTEGER, " +
                    COLUMN_LATITUDE + " REAL, " +
                    COLUMN_LONGITUDE + " REAL, " +
                    COLUMN_TEMPERATURE + " REAL, " +
                    COLUMN_HUMIDITY + " REAL, " +
                    COLUMN_PM1 + " REAL, " +
                    COLUMN_PM2 + " REAL, " +
                    COLUMN_PM10 + " REAL, " +
                    COLUMN_NOX + " REAL, " +
                    COLUMN_CO + " INTIGER, " +
                    COLUMN_VOC + " INTIGER)";

    public SensorDataDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addSensorData(Date timestamp, double latitude, double longitude,
                              double temperature, double humidity,
                              double PM, double NOX, double CO, double VOC) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, (int)(timestamp.getTime() / 1000)); //gives UNIX timestamp
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_PM1, PM);
        values.put(COLUMN_PM2, PM);
        values.put(COLUMN_PM10, PM);
        values.put(COLUMN_NOX, NOX);
        values.put(COLUMN_CO, CO);
        values.put(COLUMN_VOC, VOC);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllSensorData() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COLUMN_TIMESTAMP,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE,
                COLUMN_TEMPERATURE,
                COLUMN_HUMIDITY,
                COLUMN_PM1,
                COLUMN_PM2,
                COLUMN_PM10,
                COLUMN_NOX,
                COLUMN_CO,
                COLUMN_VOC
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getSensorDataByTimestamp(long timestamp) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COLUMN_TIMESTAMP,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE,
                COLUMN_TEMPERATURE,
                COLUMN_HUMIDITY,
                COLUMN_PM1,
                COLUMN_PM2,
                COLUMN_PM10,
                COLUMN_NOX,
                COLUMN_CO,
                COLUMN_VOC
        };
        String selection = COLUMN_TIMESTAMP + " = ?";
        String[] selectionArgs = {String.valueOf(timestamp)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        return cursor;
    }
    public Cursor getSensorDataAfterTimestamp(long timestamp) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COLUMN_TIMESTAMP,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE,
                COLUMN_TEMPERATURE,
                COLUMN_HUMIDITY,
                COLUMN_PM1,
                COLUMN_PM2,
                COLUMN_PM10,
                COLUMN_NOX,
                COLUMN_CO,
                COLUMN_VOC
        };
        String selection = COLUMN_TIMESTAMP + " >= ?";
        String[] selectionArgs = {String.valueOf(timestamp)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        return cursor;
    }
}