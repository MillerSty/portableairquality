package com.jmm.portableairquality.View;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.Model.DataEntry;
import com.jmm.portableairquality.R;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jmm.portableairquality.Model.SensorDataDatabaseHelper;

import kotlin.ParameterName;

public class HistoryView extends AppCompatActivity {
    BottomNavigationView navbot;
    LineChart chart_air;
    LineChart chart_temp;
    List<DataEntry> data;
    public final int DAY_IN_MILLIS = 3600000; //length of time that one wants to get data from in seconds
    SensorDataDatabaseHelper db;
    Calendar startTime;
    Calendar endTime;
    Button startDateSelect;
    Button startTimeSelect;
    Button endDateSelect;
    Button endTimeSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        chart_air = (LineChart) findViewById(R.id.chart_air);
        chart_temp = (LineChart) findViewById(R.id.chart_temp);
        startDateSelect = (Button) findViewById(R.id.start_button);
        startTimeSelect = (Button) findViewById(R.id.start_button2);
        endDateSelect = (Button) findViewById(R.id.end_button);
        endTimeSelect = (Button) findViewById(R.id.end_button2);

        startDateSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryView.this, (datePicker, i, i1, i2) -> {
                startTime.set(i, i1, i2); //set date year, month, day
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE)); //default is same day
            datePickerDialog.show();
        });

        startTimeSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, -10);
            TimePickerDialog timePickerDialog = new TimePickerDialog(HistoryView.this, (timePicker, i, i1) -> {
                startTime.set(Calendar.HOUR, i);
                startTime.set(Calendar.MINUTE, i1);
                if (startTime.after(Calendar.getInstance())) {
                    Toast.makeText(getApplicationContext(), "start time after current time! please change start time! resetting start time to default", Toast.LENGTH_LONG).show();
                    startTime = Calendar.getInstance();
                    startTime.add(Calendar.MINUTE, -10);
                }
            }, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), true); //default is 10 minutes earlier
            timePickerDialog.show();
        });

        endDateSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryView.this, (datePicker, i, i1, i2) -> {
                endTime.set(i, i1, i2); //set date year, month, day
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE)); //default is same day
            datePickerDialog.show();
        });

        endTimeSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(HistoryView.this, (timePicker, i, i1) -> {
                endTime.set(Calendar.HOUR, i);
                endTime.set(Calendar.MINUTE, i1);
                if (endTime.before(startTime)) {
                    Toast.makeText(getApplicationContext(), "end time before start time! please reset end time! resetting end time to present", Toast.LENGTH_LONG).show();
                    endTime = Calendar.getInstance();
                }
            }, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), true); //default is current time
            timePickerDialog.show();
        });

        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_history);
        db = SensorDataDatabaseHelper.getInstance(getApplicationContext());
        startTime = Calendar.getInstance();
        startTime.add(Calendar.MINUTE, -10);
        endTime = Calendar.getInstance();
        data = db.getEntriesBetweenTimestamps(startTime.getTimeInMillis(), endTime.getTimeInMillis());
        updateChart(data);
        registerReceiver(refresh, new IntentFilter(BluetoothHandler.UPDATED)); //basic attempt at making automated refresh
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings=new Intent(HistoryView.this, SettingsView.class);
                startActivity(goToSettings);
                return true;
            case R.id.menu_map:
                Intent goToMap = new Intent(this, MapsView.class);
                startActivity(goToMap);
                return true;
            case R.id.menu_home:
                Intent goToHome=new Intent(HistoryView.this, HomeView.class);
                startActivity(goToHome);
                return true;
            case R.id.menu_history:
                return true;
            default:
                return false;
        }
    }

    private void updateChart(List<DataEntry> historicalData) { //send this function sorted historical data of arbitrary length
        long present = new Date().getTime();
        List<Entry> co2 = new ArrayList<>();
        List<Entry> voc = new ArrayList<>();
        List<Entry> temp = new ArrayList<>();
        List<Entry> hum = new ArrayList<>();
        List<Entry> pm = new ArrayList<>();
        for (int i = 0; i < historicalData.size(); i++) { //represent the data in terms of seconds behind present
            long time = historicalData.get(i).timestamp - present;
            // if we have enough points, start taking the moving average of window size 4
            if (i > 8) {
                float co2Sum = historicalData.get(i).co2Entry;
                float vocSum = historicalData.get(i).vocEntry;
                float tempSum = historicalData.get(i).tempEntry;
                float humSum = historicalData.get(i).humEntry;
                float pmSum = historicalData.get(i).pm;
                for (int j = 0; j < 9; j++) {
                    co2Sum += co2.get(i - j - 1).getY();
                    vocSum += voc.get(i - j - 1).getY();
                    tempSum += temp.get(i - j - 1).getY();
                    humSum += hum.get(i - j - 1).getY();
                    pmSum += pm.get(i - j - 1).getY();
                }
                co2.add(new Entry(time, co2Sum/10));
                voc.add(new Entry(time, vocSum/10));
                temp.add(new Entry(time, tempSum/10));
                hum.add(new Entry(time, humSum/10));
                pm.add(new Entry(time, pmSum/10));
            } else {
                co2.add(new Entry(time, historicalData.get(i).co2Entry));
                voc.add(new Entry(time, historicalData.get(i).vocEntry));
                temp.add(new Entry(time, historicalData.get(i).tempEntry));
                hum.add(new Entry(time, historicalData.get(i).humEntry));
                pm.add(new Entry(time, historicalData.get(i).pm));
            }
        }

        // update co2 and voc chart
        LineDataSet co2Data, vocData, pmData;
        if (chart_air.getData() != null && chart_air.getData().getDataSetCount() > 0) {
            co2Data = (LineDataSet) chart_air.getData().getDataSetByIndex(0);
            vocData = (LineDataSet) chart_air.getData().getDataSetByIndex(1);
            pmData = (LineDataSet) chart_air.getData().getDataSetByIndex(2);

            co2Data.setValues(co2);
            vocData.setValues(voc);
            pmData.setValues(pm);

            chart_air.getData().notifyDataChanged();
            chart_air.notifyDataSetChanged();
        } else {
            co2Data = new LineDataSet(co2, "CO2");
            co2Data.setAxisDependency(YAxis.AxisDependency.LEFT); //set it to the left AXIS
            co2Data.setColor(0xFF4C7C, 200); // set line colour (red) and opacity
            co2Data.setDrawCircles(false);

            vocData = new LineDataSet(voc, "VOC");
            vocData.setAxisDependency(YAxis.AxisDependency.RIGHT);
            vocData.setColor(0x787EF4, 200); //light blue
            vocData.setDrawCircles(false);

            pmData = new LineDataSet(pm, "PM2.5");
            pmData.setAxisDependency(YAxis.AxisDependency.RIGHT);
            pmData.setColor(0x279119, 200); //dark green
            pmData.setDrawCircles(false);


            LineData air_data = new LineData(co2Data, vocData, pmData);
            chart_air.setData(air_data);

            XAxis xAxis_air = chart_air.getXAxis();
            xAxis_air.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:MM:ss");
                    return formatter.format(new Date((long)value + present));
                }
            });
            chart_air.setNoDataText("oh no! no data in this range, please select different dates");

            Description desc_air = chart_air.getDescription();
            desc_air.setText("Graph of air quality parameters over time!");

            chart_air.invalidate();
        }

        // update temp and humidity chart
        LineDataSet tempData, humData;
        if (chart_temp.getData() != null && chart_temp.getData().getDataSetCount() > 0) {
            tempData = (LineDataSet) chart_temp.getData().getDataSetByIndex(0);
            humData = (LineDataSet) chart_temp.getData().getDataSetByIndex(1);

            tempData.setValues(temp);
            humData.setValues(hum);

            chart_temp.invalidate();
        } else {
            tempData = new LineDataSet(temp, "Temperature");
            tempData.setAxisDependency(YAxis.AxisDependency.RIGHT);
            tempData.setColor(0x32c3e2, 200);
            tempData.setDrawCircles(false);

            humData = new LineDataSet(hum, "Relative Humidity");
            humData.setAxisDependency(YAxis.AxisDependency.RIGHT);
            humData.setColor(0x807fe2, 200);
            humData.setDrawCircles(false);

            LineData air_data = new LineData(tempData, humData);
            chart_temp.setData(air_data);

            XAxis xAxis_temp = chart_temp.getXAxis();
            xAxis_temp.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:MM:ss");
                    return formatter.format(new Date((long)value + present));
                }
            });

            chart_temp.setNoDataText("no data selected in time range, please select a different time!");

            Description desc_temp = chart_temp.getDescription();
            desc_temp.setText("Graph of temperature and humidity!");

            chart_temp.invalidate();
        }
    }

    private final BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (endTime.after(startTime)) {
                data = db.getEntriesBetweenTimestamps(startTime.getTimeInMillis(), endTime.getTimeInMillis());
                updateChart(data);
            } else {
                Toast.makeText(getApplicationContext(), "end time before start time! please reset end time! resetting end time to present", Toast.LENGTH_LONG).show();
                long present = new Date().getTime();
                data = db.getEntriesBetweenTimestamps(present - 600000, present); // show last 10 minutes
                updateChart(data);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refresh);
    }
}