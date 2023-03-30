package com.jmm.portableairquality.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jmm.portableairquality.Model.DataEntry;
import com.jmm.portableairquality.Model.HttpCallback;
import com.jmm.portableairquality.Model.SensorDataDatabaseHelper;
import com.jmm.portableairquality.Model.SensorSingleton;
import com.jmm.portableairquality.R;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class MapsFrag extends Fragment {
    List<DataEntry> data;
    public int green, yellow, red;
    private String MAPS_API_KEY = "AIzaSyBvWYYuaAHCR5Afge27E_IO7Axc2GrBqPs";
    public RequestQueue requestQueue;
    public ArrayList<String> JARJAR;
    public ArrayList<LatLng> parsedJon;
    ProgressDialog progressDialog;
    public boolean isSimulated = true;
    private int numRequests = 0;
    GoogleMap mMap;
    ArrayList<LatLng> ListLong;
    ArrayList<Integer> color;
    String httpParam;
    List<String> httpParams;
    ArrayList<LatLng> list;
    public int mPosit = 2;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        JARJAR = new ArrayList<String>();
        parsedJon = new ArrayList<LatLng>();
        list= new ArrayList<LatLng>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The File....");
        View view = inflater.inflate(R.layout.mapfrag, container, false);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("hey", Context.MODE_PRIVATE);

        SensorDataDatabaseHelper db = SensorDataDatabaseHelper.getInstance(getActivity());

        //TODO now we have date midnight, can get entries after this. Also this plus 1 day for not exceeding
        //TODO we know data works with arrayList so we can get data like above/like graphs
        //TODO Need to figure out highlighting with color
        Date todayMidnight = new Date();
//                Date tommorowMidnight = new Date();
        todayMidnight.setHours(0);
        todayMidnight.setMinutes(0);
        todayMidnight.setSeconds(1);
        List<LatLng> shortList = new ArrayList<LatLng>();
        Long midnightTimestamp = todayMidnight.getTime();

//                List for going through OUR data entries
        if (!isSimulated) {
            data = db.getEntriesAfterTimestamp(midnightTimestamp);
            color = new ArrayList<>();
            ListLong = reduceRepeats(data, color);

            httpParam=makeHttpParams(ListLong);
//            httpParams = makeHttpParams(ListLong);
        } else {
            color = new ArrayList<>();
            ListLong = reduceRepeats(data, color);

            httpParam=makeHttpParams(ListLong);
//            httpParams = makeHttpParams(ListLong);
//            try {
//                sendRequest(httpParams, new HttpCallback() {
//                    @Override
//                    public void onSuccess(LatLng array) {
//                        Log.d("ADDING", "ONSCOUSE");
//                    }
//                });
//            } catch (IOException e) {
//                progressDialog.dismiss();
//                throw new RuntimeException(e);
//            }


        }


        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);



            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    mMap = googleMap;
//                    inflateMap(ListLong,mMap);
                    try {
                        sendRequest(httpParam, new HttpCallback() {
                            @Override
                            public void onSuccess(ArrayList<LatLng> array) {
                                Log.d("ADDING", "ONSCOUSE");
                                if(numRequests==0){
                                inflateMap(array,mMap);}
                            }
                        });
                    } catch (IOException e) {
                        progressDialog.dismiss();
                        throw new RuntimeException(e);
                    }

//                        mMap = inflateMap(ListLong, mMap);
                        Log.d("ADDING", "INFLATION ERROR");


                }
            });

        return view;

    }

    public ArrayList<LatLng> reduceRepeats(List<DataEntry> data, ArrayList<Integer> color) {
        ArrayList<LatLng> dataArray = new ArrayList<>();
        ArrayList<DataEntry> d = new ArrayList<DataEntry>();
        if (isSimulated) {
            ArrayList<LatLng> isSimulate = getSimulated();
            for (int k = 0; k < isSimulate.size(); k++) {
                color.add(sortColor(d, k));
            }
            return isSimulate;
        } else {
            LatLng base = new LatLng(data.get(0).latitude, data.get(0).longitude);
            dataArray.add(base);
//            color.add(sortColor(data, 0));
            for (int k = 1; k < data.size() - 1; k++) {
                if (data.get(k).latitude != data.get(k - 1).latitude &&
                        data.get(k).longitude != data.get(k - 1).longitude) {
                    //Lat long data
                    double latitude = data.get(k).latitude;
                    double longitude = data.get(k).longitude;
                    dataArray.add(new LatLng(latitude, longitude));
//                    color.add(sortColor(data, k));
                }
            }
            return dataArray;
        }

    }

    public GoogleMap inflateMap(ArrayList<LatLng> arrayList, GoogleMap googleMap) {

        float[] k = new float[3];
        MarkerOptions markerOptions = new MarkerOptions();
        googleMap.clear();
        markerOptions.position(ListLong.get(0));
        HSVGetter(color.get(0), k);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(k[0]));
        int position = ListLong.size() - 1;
        googleMap.addPolyline(new PolylineOptions().color(color.get(0)).add(ListLong.get(0))
        );
        //zooms to newest position
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ListLong.get(position), 20F));
//        Log.d("ADDING2",arrayList.toString());
        PolylineOptions line=new PolylineOptions();

        for (int i = 1; i < this.ListLong.size(); i++) {
//            line.add(arrayList.get(i));
//                    line.color(color.get(i));
            //only need markers for original positions
            markerOptions.position(ListLong.get(i));
//            mMap.addPolyline(new PolylineOptions().color(color.get(i - 1))
//                    .add(ListLong.get(i),ListLong.get(i-1))
//                    .width(5)
//            );
//            HSVGetter(color.get(i), k);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(k[0]));
            googleMap.addMarker(markerOptions);
//            googleMap.addPolyline(line);
        }
//        googleMap.addPolyline(line);
        mMap.addPolyline(new PolylineOptions().color(color.get(0))
                .addAll(arrayList)
                .width(5)
        );
        return mMap;

    }


    public String makeHttpParams(List<LatLng> ListLong) {
        List<String> httpParams = new ArrayList<String>();
//        String httpParam= new String();
httpParam="";
        for (int k = 0; k < ListLong.size() ; k++) {
            double curlat, curlong, prelat, prelong;
            curlat = ListLong.get(k).latitude;
            curlong = ListLong.get(k).longitude;
            if(k!=ListLong.size()-1){
            httpParam=httpParam+curlat+","+curlong+"|";}
            else{ httpParam=httpParam+curlat+","+curlong;}
//            prelat = ListLong.get(k - 1).latitude;
//            prelong = ListLong.get(k - 1).longitude;
//            httpParams.add(prelat + "," + prelong + "|"
//                    + curlat + "," + curlong);
        }
        return httpParam;
    }

    public ArrayList<LatLng> getSimulated() {
        ArrayList<LatLng> ListLong = new ArrayList<LatLng>();
//        ListLong.add(new LatLng(45.52980, -73.58083));
//        ListLong.add(new LatLng(45.53018, -73.58167));
//        ListLong.add(new LatLng(45.53062, -73.58092));
//        ListLong.add(new LatLng(45.53124, -73.58052));
//        ListLong.add(new LatLng(45.53192, -73.58046));
//        ListLong.add(new LatLng(45.53225, -73.58133));


        ListLong.add(new LatLng(45.52806,-73.57705));
        ListLong.add(new LatLng(45.52953,-73.57526));
        ListLong.add(new LatLng(45.53041,-73.57291));
        ListLong.add(new LatLng(45.53017,-73.57096));
        ListLong.add(new LatLng(45.52921,-73.56848));
        ListLong.add(new LatLng(45.52767,-73.56568));
        ListLong.add(new LatLng(45.52657,-73.56439));
        ListLong.add(new LatLng(45.52518,-73.56484));
        ListLong.add(new LatLng(45.52378,-73.56526));
        ListLong.add(new LatLng(45.52240,-73.56558));
        ListLong.add(new LatLng(45.52251,-73.56768));
        ListLong.add(new LatLng(45.52327,-73.56942));
        ListLong.add(new LatLng(45.52406,-73.57112));
        ListLong.add(new LatLng(45.52487,-73.57292));
        return ListLong;


    }

    private void loadDogImage(String house, HttpCallback httpCallback) {
        String url = house;
        list = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                (Response.Listener<JSONObject>) response -> {
                    try {
//                        if(mPosit>=ListLong.size()){mPosit=ListLong.size()-2;}else if(mPosit==ListLong.size()-1){mPosit=ListLong.size()-3;}

                        //let ListLong.size()=15
//                        for (int j = mPosit-2; j < mPosit ; j++) {
                            final String finaly=house;
                            JSONArray jarray = response.getJSONArray("snappedPoints");



                        color.add(sortColor(data, 0));
                        color.add(sortColor(data, 0));
                            for (int i = 0; i < jarray.length(); i++) {
//                                list.add(ListLong.get(mPosit-2));
                                JSONObject jorry = jarray.getJSONObject(i);
                                float[] k = new float[3];
                                color.add(sortColor(data, 0));

                                double latitude = jorry.getJSONObject("location").getDouble("latitude");
                                double longitude = jorry.getJSONObject("location").getDouble("longitude");
                                LatLng newLatLng = new LatLng(latitude, longitude);
                                list.add(newLatLng);
//                                list.add(ListLong.get(mPosit));
                                //add colour error with no

                                Log.d("ADDING1", i + " " +list.get(i).toString());
//                                if(mPosit<=ListLong.size()-3){
//                                    mPosit+=2;
//                                }
                            }

//                        }

                        //add 2 to mPosit
//                        if(mPosit>=ListLong.size()){mPosit=ListLong.size()-2;}else if(mPosit==ListLong.size()-1){mPosit=ListLong.size()-3;}

//                        if(mPosit>=15){mPosit=13;}else if(mPosit==14){mPosit=12;}
//                        mPosit=ListLong.indexOf(ListLong.get(mPosit));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                    numRequests--;

                        httpCallback.onSuccess(list);
                    progressDialog.dismiss();
                    Log.d("ADDING", "REQUEST TO TRUE?");
                },

                (Response.ErrorListener) error -> {
                    Log.e("ADDING", String.format("loadDogImage url %s: " + error.getLocalizedMessage(), house));
                    progressDialog.dismiss();
                }
        );
        Log.d("ADDING", "REQUEST TO QUEU");

        requestQueue.add(jsonObjectRequest);
        progressDialog.show();
        numRequests++;
    }


    public void sendRequest(String list, HttpCallback httpCallback) throws IOException {

            String PARAMS = list;
            String house;
            house = String.format("https://roads.googleapis.com/v1/snapToRoads?path=%s&interpolate=true&key=%s", PARAMS, MAPS_API_KEY);
            loadDogImage(house, httpCallback);


    }

    //-9247099 green
    //-2100620 yellow
    //-1040896 red
    public void HSVGetter(int color, float[] hsv) {
//            Color.RGBToHSV();
        //TODO NOTE: if we redo our colors green and yellow we have to recalc the
        int r, g, b;
        if (!isSimulated) {
            if (color == green) {
                hsv[0] = 130;
                hsv[1] = 50;
                hsv[2] = 90;
//            return hsv;
            } else if (color == yellow) {
//            hsv(69,52,95)
                hsv[0] = 69;
                hsv[1] = 52;
                hsv[2] = 95;
//            return hsv;
            } else {
//            hsv(8,100,94)
                hsv[0] = 8;
                hsv[1] = 100;
                hsv[2] = 94;
//            return hsv;
            }
        } else {
            hsv[0] = 130;
            hsv[1] = 50;
            hsv[2] = 90;

        }

    }

    public int sortColor(List<DataEntry> dataEntry, int position) {

//    int avgPollutants = (dataEntry.get(position).co2Entry + dataEntry.get(position).vocEntry ) / 2;
        if (!isSimulated) {
            int avgPollutants = (dataEntry.get(position).co2Entry + dataEntry.get(position).vocEntry + (int) dataEntry.get(position).pm) / 3;

            if (avgPollutants <= SensorSingleton.GlobalGreen) {
                int val = getResources().getColor(R.color.green_200);
                green = val;
                return val; //2131034216
            } else if (avgPollutants <= SensorSingleton.GlobalYellow) {
                int val = getResources().getColor(R.color.yellow_200);
                yellow = val;
                return val;//2131034771
            } else {
                int val = getResources().getColor(R.color.red_200);
                red = val;
                return val;//2131034753
            }
        } else {
            int val = getResources().getColor(R.color.red_200);
            green = val;
            return val;

        }

    }

    void deadWorkingSamples() {

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

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
