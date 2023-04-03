package com.jmm.portableairquality.View;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.Model.DataEntry;
import com.jmm.portableairquality.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jmm.portableairquality.Model.SensorDataDatabaseHelper;

//charts by https://github.com/PhilJay/MPAndroidChart
public class HistoryView extends AppCompatActivity {
    BottomNavigationView navbot;
    LineChart chart_air;
    LineChart chart_temp;
    List<DataEntry> data;
    SensorDataDatabaseHelper db;
    public SharedPreferences sharedPref;
    int textColor;
    Calendar startTime;
    Calendar endTime;
    EditText startDateSelect;
    EditText startTimeSelect;
    EditText endDateSelect;
    EditText endTimeSelect;
    Button resetButton;
    Boolean live = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedPref = this.getSharedPreferences("graphColour", Context.MODE_PRIVATE);

        chart_air = (LineChart) findViewById(R.id.chart_air);
        chart_temp = (LineChart) findViewById(R.id.chart_temp);

        startDateSelect = (EditText) findViewById(R.id.start_button);
        startTimeSelect = (EditText) findViewById(R.id.start_button2);
        endDateSelect = (EditText) findViewById(R.id.end_button);
        endTimeSelect = (EditText) findViewById(R.id.end_button2);

        // this is necessary so that the keyboard doesn't appear
        startDateSelect.setFocusable(false);
        startTimeSelect.setFocusable(false);
        endDateSelect.setFocusable(false);
        endTimeSelect.setFocusable(false);

        resetButton = (Button) findViewById(R.id.reset_button);

        startDateSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, -24);

            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryView.this, (datePicker, i, i1, i2) -> {
                startTime.set(i, i1, i2); //set date year, month, day
                startDateSelect.setText(new SimpleDateFormat("MMM dd").format(startTime.getTimeInMillis()));
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE)); //default is same day
            datePickerDialog.show();

            chart_air.fitScreen();
            chart_temp.fitScreen();
        });

        startTimeSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, -24);

            TimePickerDialog timePickerDialog = new TimePickerDialog(HistoryView.this, (timePicker, i, i1) -> {
                startTime.set(Calendar.HOUR, i);
                startTime.set(Calendar.MINUTE, i1);
                if (startTime.after(Calendar.getInstance())) {
                    Toast.makeText(getApplicationContext(), "start time after current time! please change start time! resetting start time to default", Toast.LENGTH_LONG).show();
                    startTime = Calendar.getInstance();
                    startTime.add(Calendar.MINUTE, -10);
                }
                startTimeSelect.setText(new SimpleDateFormat("HH:mm").format(startTime.getTimeInMillis()));
            }, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), true); //default is 10 minutes earlier

            timePickerDialog.show();

            chart_air.fitScreen();
            chart_temp.fitScreen();
        });

        endDateSelect.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryView.this, (datePicker, i, i1, i2) -> {
                endTime.set(i, i1, i2); //set date year, month, day
                endDateSelect.setText(new SimpleDateFormat("MMM dd").format(endTime.getTimeInMillis()));
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE)); //default is same day

            datePickerDialog.show();

            chart_air.fitScreen();
            chart_temp.fitScreen();

            live = false;
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
                endTimeSelect.setText(new SimpleDateFormat("HH:mm").format(endTime.getTimeInMillis()));
            }, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), true); //default is current time

            timePickerDialog.show();

            chart_air.fitScreen();
            chart_temp.fitScreen();

            live = false;
        });

        resetButton.setOnClickListener(view -> {
            startTime = Calendar.getInstance();
            startTime.add(Calendar.HOUR, -24);
            endTime = Calendar.getInstance();

            // reset the text in the boxes to match
            startDateSelect.setText(new SimpleDateFormat("MMM dd").format(startTime.getTimeInMillis()));
            startTimeSelect.setText(new SimpleDateFormat("HH:mm").format(startTime.getTimeInMillis()));
            endDateSelect.setText("Now");
            endTimeSelect.setText(" ");

            chart_air.fitScreen();
            chart_temp.fitScreen();

            live = true;
        });

        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_history);
        db = SensorDataDatabaseHelper.getInstance(getApplicationContext());

        TypedValue type=new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary,type,true);
        textColor= ContextCompat.getColor(this,type.resourceId);

        startTime = Calendar.getInstance();
        startTime.add(Calendar.HOUR, -24);
        endTime = Calendar.getInstance();
        data = db.getEntriesBetweenTimestamps(startTime.getTimeInMillis(), endTime.getTimeInMillis());

        updateChart(data);
        registerReceiver(refresh, new IntentFilter(BluetoothHandler.UPDATED)); //basic attempt at making automated refresh

        startDateSelect.setText(new SimpleDateFormat("MMM dd").format(startTime.getTimeInMillis()));
        startTimeSelect.setText(new SimpleDateFormat("HH:mm").format(startTime.getTimeInMillis()));
        endDateSelect.setText(new SimpleDateFormat("MMM dd").format(endTime.getTimeInMillis()));
        endTimeSelect.setText(new SimpleDateFormat("HH:mm").format(endTime.getTimeInMillis()));
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings=new Intent(HistoryView.this, SettingsView.class);
                startActivity(goToSettings, ActivityOptions.makeSceneTransitionAnimation(HistoryView.this).toBundle());
                return true;
            case R.id.menu_map:
                Intent goToMap = new Intent(this, MapsView.class);
                startActivity(goToMap, ActivityOptions.makeSceneTransitionAnimation(HistoryView.this).toBundle());
                return true;
            case R.id.menu_home:
                Intent goToHome=new Intent(HistoryView.this, HomeView.class);
                startActivity(goToHome, ActivityOptions.makeSceneTransitionAnimation(HistoryView.this).toBundle());
                return true;
            case R.id.menu_history:
                return true;
            default:
                return false;
        }
    }

    @SuppressLint("ResourceType")
    private void updateChart(List<DataEntry> historicalData) { //send this function sorted historical data of arbitrary length
        List<Entry> co2 = new ArrayList<>();
        List<Entry> voc = new ArrayList<>();
        List<Entry> temp = new ArrayList<>();
        List<Entry> hum = new ArrayList<>();
        List<Entry> pm = new ArrayList<>();

        if (historicalData.size() > 0) {
            long latest = historicalData.get(historicalData.size() - 1).timestamp;

            for (int i = 0; i < historicalData.size(); i++) { //represent the data in terms of seconds behind present
                long time = historicalData.get(i).timestamp - latest;
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

                    co2.add(new Entry(time, co2Sum / 10));
                    voc.add(new Entry(time, vocSum / 10));
                    temp.add(new Entry(time, tempSum / 10));
                    hum.add(new Entry(time, humSum / 10));
                    pm.add(new Entry(time, pmSum / 10));
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

                chart_air.invalidate();
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
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        long millis = (long) value + latest;
                        return formatter.format(new Date(millis));
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
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        return formatter.format(new Date((long) value + latest));
                    }
                });

                chart_temp.setNoDataText("no data selected in time range, please select a different time!");

                Description desc_temp = chart_temp.getDescription();
                desc_temp.setText("Graph of temperature and humidity!");

                chart_temp.invalidate();
            }
        }
    }

    private final BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(live) {
                endTime = Calendar.getInstance();
            }

            if (endTime.before(startTime)) {
                Toast.makeText(getApplicationContext(), "end time before start time! please reset end time! resetting end time to present", Toast.LENGTH_LONG).show();
                endTime = Calendar.getInstance();
                if (startTime.after(endTime)) {
                    startTime = Calendar.getInstance();
                    startTime.add(Calendar.HOUR, -24);
                }
            }

            data = db.getEntriesBetweenTimestamps(startTime.getTimeInMillis(), endTime.getTimeInMillis());

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