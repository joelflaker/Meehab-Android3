package com.citrusbits.meehab.adapters;

import android.app.Activity;
import android.view.View;

import com.citrusbits.meehab.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class RehabInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity mActivity;

    public RehabInfoWindowAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = mActivity.getLayoutInflater().inflate(R.layout.map_rehab_info_window, null);

        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
