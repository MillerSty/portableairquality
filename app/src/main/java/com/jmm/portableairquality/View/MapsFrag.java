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
        PolylineOptions[] polylineOptions = {new PolylineOptions()};
        //zooms to newest position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arrayList.get(position), 20F));

        for (int i = 0; i < arrayList.size(); i++) {
            polylineOptions[0].color(color.get(i)).add(arrayList.get(i)).width(10);
            mMap.addPolyline(polylineOptions[0]);
            if (i % 5 != 0) {
                polylineOptions[0] = new PolylineOptions();
                polylineOptions[0].add(arrayList.get(i)).color(color.get(i));
                mMap.addPolyline(polylineOptions[0]);
            }
        }

        MapStyleOptions viewmode;
        if(!swissPref.getBoolean("swiss",false)){
            viewmode=MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.retro);
        }
        else{
            viewmode=MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.darkmode);
        }
        mMap.setMapStyle(viewmode);
        return mMap;
    }

    public int sortColor(List<DataEntry> dataEntry, int position) {
        //TODO Dial in better global colors

        int co2 = dataEntry.get(position).co2Entry;
        int voc = dataEntry.get(position).vocEntry;
        int pm = (int) dataEntry.get(position).pm;

        int colour = R.color.green_200;

        if (co2 > 2000 || voc > 200 || pm > 12) { colour = R.color.yellow_200; }
        if (co2 > 6000 || voc > 600 || pm > 35) { colour = R.color.red_200; }

        return getResources().getColor(colour);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
