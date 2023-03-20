package com.jmm.portableairquality.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.Model.DataEntry;
import com.jmm.portableairquality.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jmm.portableairquality.Model.SensorDataDatabaseHelper;

public class HistoryView extends AppCompatActivity {
    BottomNavigationView navbot;
    LineChart chart_air;
    LineChart chart_temp;
    List<DataEntry> data;
    long period = 1000; //length of time that one wants to get data from in seconds
    SensorDataDatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        chart_air = (LineChart) findViewById(R.id.chart_air);
        chart_temp = (LineChart) findViewById(R.id.chart_temp);
        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_history);
        db = SensorDataDatabaseHelper.getInstance(getApplicationContext());
        data = db.getEntriesAfterTimestamp(new Date().getTime() - (1000*period));
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
//                showToast("CLICKED SETTINGS");
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
        long present = historicalData.get(historicalData.size()-1).timestamp;
        List<Entry> co2 = new ArrayList<Entry>();
        List<Entry> voc = new ArrayList<Entry>();
        List<Entry> temp = new ArrayList<Entry>();
        List<Entry> hum = new ArrayList<Entry>();
        for (int i = 0; i < historicalData.size(); i++) { //represent the data in terms of seconds behind present
            historicalData.get(i).timestamp -= present;
            co2.add(new Entry(historicalData.get(i).timestamp/1000, historicalData.get(i).co2Entry));
            voc.add(new Entry(historicalData.get(i).timestamp/1000, historicalData.get(i).vocEntry));
            temp.add(new Entry(historicalData.get(i).timestamp/1000, historicalData.get(i).tempEntry));
            voc.add(new Entry(historicalData.get(i).timestamp/1000, historicalData.get(i).humEntry));
        }

        LineDataSet co2Data = new LineDataSet(co2, "eCO2");
        co2Data.setAxisDependency(YAxis.AxisDependency.LEFT); //set it to the left AXIS
        co2Data.setColor(0xFF4C7C, 200); // set line colour and opacity
        co2Data.setCircleColor(0xFF4C7C); // this disables the circles for some reason
        co2Data.setCubicIntensity(0.2f); // i have no idea what this does

        LineDataSet vocData = new LineDataSet(voc, "VOCs");
        vocData.setAxisDependency(YAxis.AxisDependency.RIGHT);
        vocData.setColor(0x787EF4, 200);
        vocData.setCircleColor(0x787EF4);

        LineDataSet tempData = new LineDataSet(temp, "Temperature");
        tempData.setAxisDependency(YAxis.AxisDependency.RIGHT);
        tempData.setColor(0x787EF4, 200);
        tempData.setCircleColor(0x787EF4);

        LineDataSet humData = new LineDataSet(hum, "Relative Humidity");
        humData.setAxisDependency(YAxis.AxisDependency.RIGHT);
        humData.setColor(0x787EF4, 200);
        humData.setCircleColor(0x787EF4);

        List<ILineDataSet> sets_air = new ArrayList<>();
        sets_air.add(co2Data);
        sets_air.add(vocData);

        List<ILineDataSet> sets_temp = new ArrayList<>();
        sets_temp.add(tempData);
        sets_temp.add(humData);

        LineData allData_air = new LineData(sets_air);
        LineData allData_temp = new LineData(sets_temp);

        chart_air.setData(allData_air);
        chart_air.setNoDataText("oh no! no data :(");
        Description desc = chart_air.getDescription();
        desc.setText("Graph of air quality parameters over the time!");
        chart_air.invalidate();

        chart_temp.setData(allData_temp);
        chart_temp.setNoDataText("no data, something is amiss here");
        Description desc_temp = chart_air.getDescription();
        desc_temp.setText("Graph of temperature and humidity!");
        chart_temp.invalidate();
    }

    private final BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            data = db.getEntriesAfterTimestamp(new Date().getTime() - (1000*period));
            if (data.size() > 0) {
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