package com.citrusbits.meehab.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.citrusbits.meehab.prefrences.AppPrefs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {
	public static final String TAG = LocationService.class.getSimpleName();

	private GoogleApiClient mGoogleApiClient;

	List<LocationListener> listeners = new ArrayList<LocationListener>();

	MyLocalBinder binder = new MyLocalBinder();

	AppPrefs prefs;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		mGoogleApiClient.connect();
		prefs = AppPrefs.getAppPrefs(LocationService.this);
		Log.e(TAG, "OnCrate");

	}

	public class MyLocalBinder extends Binder {

		public LocationService getService() {
			return LocationService.this;
		}
	}

	public void addListener(LocationListener listenere) {
		this.listeners.add(listenere);
	}

	public void removeListener(LocationListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onStartCommand");

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.e(TAG, "Location is changed");
		prefs.saveDoublePrefs(AppPrefs.KEY_LATITUDE, location.getLatitude());
		prefs.saveDoublePrefs(AppPrefs.KEY_LONGITUDE, location.getLongitude());

		for (LocationListener listener : listeners) {
			listener.onChangeLocation(location);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "Connectin Failed");

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, REQUEST, this);
		Log.e(TAG, "Connected");

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "Connectin Suspended");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Log.e(TAG, "Service is destroyed");
		
		
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	public interface LocationListener {
		public void onChangeLocation(Location location);
	}

}
