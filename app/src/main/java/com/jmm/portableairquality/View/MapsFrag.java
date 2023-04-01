package com.jmm.portableairquality.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
    public boolean isSimulated = false;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapfrag, container, false);

        //getting midnight timestamp for database fetch
        SensorDataDatabaseHelper db = SensorDataDatabaseHelper.getInstance(getActivity());
        Date todayMidnight = new Date();
        todayMidnight.setHours(0);
        todayMidnight.setMinutes(0);
        todayMidnight.setSeconds(1);
        Long midnightTimestamp = todayMidnight.getTime();

        //  get data from after timestamp and then reduce
            data = db.getEntriesAfterTimestamp(midnightTimestamp);
            color = new ArrayList<>();
            ListLong = reduceRepeats(data, color);


        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                mMap = inflateMap(ListLong, mMap);
            }
        });
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
        int position = arrayList.size() - 1;
        //zooms to newest position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arrayList.get(position), 20F));
        for (int i = 0; i < arrayList.size(); i++) {
            markerOptions.position(arrayList.get(i));
            line.color(color.get(i)).add(arrayList.get(i)).width(10);
            mMap.addPolyline(line);
        }
        return mMap;

    }



    public int sortColor(List<DataEntry> dataEntry, int position) {
        //TODO Dial in better global colors
//    int avgPollutants = (dataEntry.get(position).co2Entry + dataEntry.get(position).vocEntry ) / 2;

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

    void deadWorkingSamples() {
//Keeping for now incase micah needs to revisit maps
//          More simulated lat/lngs
//        ListLong.add(new LatLng(45.52806, -73.57705));
//        ListLong.add(new LatLng(45.52953, -73.57526));
//        ListLong.add(new LatLng(45.53041, -73.57291));
//        ListLong.add(new LatLng(45.53017, -73.57096));
//        ListLong.add(new LatLng(45.52921, -73.56848));
//        ListLong.add(new LatLng(45.52767, -73.56568));
//        ListLong.add(new LatLng(45.52657, -73.56439));
//        ListLong.add(new LatLng(45.52518, -73.56484));
//        ListLong.add(new LatLng(45.52378, -73.56526));
//        ListLong.add(new LatLng(45.52240, -73.56558));
//        ListLong.add(new LatLng(45.52251, -73.56768));
//        ListLong.add(new LatLng(45.52327, -73.56942));
//        ListLong.add(new LatLng(45.52406, -73.57112));
//        ListLong.add(new LatLng(45.52487, -73.57292));
        //Simulated route
        //SEEMS TO WORK 100% of time
//                List<LatLng> ListLong = new ArrayList<LatLng>();
//                ListLong.add(new LatLng(45.529980160000000921, -73.580938340000002994));
//                ListLong.add(new LatLng(45.530894824252194, -73.5836609379996));
//                ListLong.add(new LatLng(45.53513176707751, -73.5794210374006));
//                ListLong.add(new LatLng(45.53148180005181, -73.59008790091859));
//                MarkerOptions markerOptions = new MarkerOptions();
//                googleMap.clear();
//                markerOptions.position(ListLong.get(0));
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ListLong.get(0), 20F));
//                PolylineOptions line= new PolylineOptions();
//                line.color(Color.RED);
//                line.width(5);
//                googleMap.addMarker(markerOptions);
//                line.add(ListLong.get(0));
//                for (int i = 1; i < ListLong.size(); i++) {
////                    option.add(ListLong.get(i));
//
//                    markerOptions.position(ListLong.get(i));
//                    line.add(ListLong.get(i));
//                    googleMap.addMarker(markerOptions);
////                googleMap.clear();//
//                }

//

        //THIS SEEMS TO WORK 100% of the time
//                LatLng latlong = new LatLng(Double.longBitsToDouble(sharedPref.getLong("Lat", 0)), Double.longBitsToDouble(sharedPref.getLong("Long", 0)));
//                MarkerOptions markerOptions = new MarkerOptions();
//                googleMap.clear();
//                markerOptions.position(latlong);
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 20F));
//                PolylineOptions line= new PolylineOptions();
//                line.color(Color.RED);
//                line.width(5);
//                googleMap.addMarker(markerOptions);
//                line.add(latlong);

//               // global addPolyLine
//                    googleMap.addPolyline(line);}
    }

}
