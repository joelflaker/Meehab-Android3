package com.citrusbits.meehab.ui.customviews;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;

public class CustomMapFragment extends Fragment {

	CustomMapView view;
	Bundle bundle;
	GoogleMap map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = savedInstanceState;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// View v = inflater.inflate(R.layout.fragment_map, container, false);

		// / view = (CustomMapView) v.findViewById(R.id.mapView);
		view = new CustomMapView(getActivity());
		view.onCreate(bundle);
		view.onResume();

		map = view.getMap();
		view.init(map);

		MapsInitializer.initialize(getActivity());

		// return v;

		return view;
	}

	public GoogleMap getMap() {
		return map;
	}

	@Override
	public void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		view.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		view.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		view.onLowMemory();
	}
}