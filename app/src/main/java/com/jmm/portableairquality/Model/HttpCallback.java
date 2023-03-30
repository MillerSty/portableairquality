package com.jmm.portableairquality.Model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public interface HttpCallback {
    public void onSuccess(ArrayList<LatLng> array);


}
