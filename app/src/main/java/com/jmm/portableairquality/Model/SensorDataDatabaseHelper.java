package com.jmm.portableairquality.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
                    COLUMN_TIMESTAMP + " INTEGER," +
                    COLUMN_LATITUDE + " REAL," +
                    COLUMN_LONGITUDE + " REAL," +
                    COLUMN_TEMPERATURE + " REAL," +
                    COLUMN_HUMIDITY + " REAL," +
                    COLUMN_PM1 + " REAL," +
                    COLUMN_PM2 + " REAL," +
                    COLUMN_PM10 + " REAL," +
                    COLUMN_NOX + " REAL," +
                    COLUMN_CO + " INTEGER," +
                    COLUMN_VOC + " INTEGER)";

    static SensorDataDatabaseHelper instance;

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

    public static synchronized SensorDataDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SensorDataDatabaseHelper(context);
        }
        return instance;
    }

    public void addSensorData(Date timestamp, double latitude, double longitude,
                              double temperature, double humidity,
                              double PM1 , double PM2, double PM10, double NOX, double CO, double VOC) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, (timestamp.getTime())); //gives UNIX timestamp, doesn't need to be divided, already in ms
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_PM1, PM1);
        values.put(COLUMN_PM2, PM2);
        values.put(COLUMN_PM10, PM10);
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
        // Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);
        Cursor cursor = builder.query(db, columns, selection,
                selectionArgs, null, null, null);
        return cursor;
    }

    public List<DataEntry> getEntriesAfterTimestamp(long timestamp) {
        Cursor cursor = getSensorDataAfterTimestamp(timestamp);
        List<DataEntry> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isLast(); cursor.moveToNext()) {
                int co2Entry = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CO));
                int vocEntry = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VOC));
                float tempEntry = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE));
                float humEntry = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HUMIDITY));
                float pm = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PM10));
                long timestmp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

                DataEntry dataEntry = new DataEntry(co2Entry, vocEntry, tempEntry, humEntry, pm, timestmp);
                list.add(dataEntry);
            }
        }

        return list;
    }
}