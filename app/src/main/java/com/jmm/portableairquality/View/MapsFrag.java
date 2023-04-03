package com.jmm.portableairquality.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.Model.DataEntry;
import com.jmm.portableairquality.Model.SensorDataDatabaseHelper;
import com.jmm.portableairquality.Model.SensorSingleton;
import com.jmm.portableairquality.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsFrag extends Fragment {
     public int green, yellow, red;
    GoogleMap mMap;
    List<DataEntry> data;
    ArrayList<LatLng> ListLong;
    ArrayList<Integer> color;
    SharedPreferences swissPref;
    final int DAY_IN_MILLIS = 3600000;
    BottomNavigationView navbot;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfrag, container, false);
        swissPref= getContext().getSharedPreferences("switch", Context.MODE_PRIVATE);
        //getting midnight timestamp for database fetch
        SensorDataDatabaseHelper db = SensorDataDatabaseHelper.getInstance(getActivity());
        navbot=getActivity().findViewById(R.id.bottom_nav);

        //  get data from after timestamp and then reduce
        data = db.getEntriesAfterTimestamp(new Date().getTime() - DAY_IN_MILLIS);

        if (data.size() > 0) {
            color = new ArrayList<>();
            ListLong = reduceRepeats(data, color);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
            supportMapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mMap = inflateMap(ListLong, mMap);
            });
        }
        return view;
    }

    public ArrayList<LatLng> reduceRepeats(List<DataEntry> data, ArrayList<Integer> color) {
        ArrayList<LatLng> dataArray = new ArrayList<>();
        List<DataEntry> data1 = new ArrayList<>();

        //add non zero lat/lngs
        for (int k = 0; k < data.size(); k++) {
            if (!(data.get(k).latitude == 0) || !(data.get(k).longitude == 0)) {
                data1.add(data.get(k));
            }
        }

        //add first non zero lat lng and add its color to color array
        LatLng base = new LatLng(data1.get(0).latitude, data1.get(0).longitude);
        dataArray.add(base);
        color.add(sortColor(data1, 0));

        //if lat/lng does not equal last lat/lng add it to our path and calculate color
        for (int k = 1; k < data1.size() - 1; k++) {
            if (data1.get(k).latitude != data1.get(k - 1).latitude &&
                    data1.get(k).longitude != data1.get(k - 1).longitude) {
                double latitude = data1.get(k).latitude;
                double longitude = data1.get(k).longitude;
                dataArray.add(new LatLng(latitude, longitude));
                color.add(sortColor(data1, k));
            }
        }

        return dataArray;
    }

    public GoogleMap inflateMap(ArrayList<LatLng> arrayList, GoogleMap googleMap) {

        PolylineOptions line = new PolylineOptions();
        MarkerOptions markerOptions = new MarkerOptions();
        mMap.clear();
        int position;
        position = arrayList.size() - 1;

        //zooms to newest position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arrayList.get(position), 20F));

        for (int i = 0; i < arrayList.size(); i++) {
            markerOptions.position(arrayList.get(i));
            line.color(color.get(i)).add(arrayList.get(i)).width(10);
            mMap.addPolyline(line);
        }
        MapStyleOptions viewmode;
        if(!swissPref.getBoolean("swiss",false)){
                    viewmode=MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.retro);
        }
        else{                    viewmode=MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.darkmode);

        }
        mMap.setMapStyle(viewmode);
        return mMap;
    }

    public int sortColor(List<DataEntry> dataEntry, int position) {
        //TODO Dial in better global colors

        int avgPollutants = (dataEntry.get(position).co2Entry + dataEntry.get(position).vocEntry + (int) dataEntry.get(position).pm) / 3;

        if (avgPollutants <= (SensorSingleton.GlobalGreen + 1000)) {
            int val = getResources().getColor(R.color.green_200);
            green = val;
            return val; //2131034216
        } else if (avgPollutants <= (1000 + SensorSingleton.GlobalYellow)) {
            int val = getResources().getColor(R.color.yellow_200);
            yellow = val;
            return val;//2131034771
        } else {
            int val = getResources().getColor(R.color.red_200);
            red = val;
            return val;//2131034753
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
