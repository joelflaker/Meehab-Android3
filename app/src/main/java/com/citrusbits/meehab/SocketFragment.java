package com.citrusbits.meehab;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.services.SocketService;
import com.citrusbits.meehab.services.SocketService.SocketServiceConnectionBinder;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.instabug.wrapper.support.activity.InstabugFragmentActivity;


public abstract class SocketFragment extends Fragment {

	protected static final int REQUEST_ANNOUNCE_PGP = 0x0101;
	protected static final int REQUEST_INVITE_TO_CONVERSATION = 0x0102;

	public SocketService socketService;
	public boolean socketConnectionServiceBound = false;
	protected boolean registeredListeners = false;

	protected boolean mUseSubject = true;

	private DisplayMetrics metrics;
	protected boolean mUsingEnterKey = false;

	protected ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			SocketServiceConnectionBinder binder = (SocketServiceConnectionBinder) service;
			socketService = binder.getService();
			socketConnectionServiceBound = true;
			if (!registeredListeners && shouldRegisterListeners()) {
				registerListeners();
				registeredListeners = true;
			}
			onBackendConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			socketConnectionServiceBound = false;
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		if (!socketConnectionServiceBound) {
			connectToBackend();
		} else {
			if (!registeredListeners) {
				this.registerListeners();
				this.registeredListeners = true;
			}
			this.onBackendConnected();
		}
	}

	protected boolean shouldRegisterListeners() {
			return !isDetached() && !isRemoving();
	}

	public void connectToBackend() {
		Intent intent = new Intent(getActivity(), SocketService.class);
		intent.setAction("ui");
		getActivity().startService(intent);
		getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (socketConnectionServiceBound) {
			if (registeredListeners) {
				this.unregisterListeners();
				this.registeredListeners = false;
			}
			getActivity().unbindService(mConnection);
			socketConnectionServiceBound = false;
		}
	}


	void onBackendConnected(){
	}

	protected void registerListeners() {
		if (this instanceof SocketService.OnConversationUpdate) {
			this.socketService.setOnConversationListChangedListener((SocketService.OnConversationUpdate) this);
		}
		if (this instanceof SocketService.OnAccountUpdate) {
			this.socketService.setOnAccountListChangedListener((SocketService.OnAccountUpdate) this);
		}
		if (this instanceof OnSocketResponseListener) {
			this.socketService.setOnSocketResponseListener((OnSocketResponseListener) this);
		}
	}

	protected void unregisterListeners() {
		if (this instanceof SocketService.OnConversationUpdate) {
			this.socketService.removeOnConversationListChangedListener();
		}
		if (this instanceof SocketService.OnAccountUpdate) {
			this.socketService.removeOnAccountListChangedListener();
		}
		if (this instanceof OnSocketResponseListener) {
			this.socketService.removeOnSocketResponseListener();
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		metrics = getResources().getDisplayMetrics();
	}

	protected SharedPreferences getPreferences() {
		return getActivity().getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
	}

	public boolean useSubjectToIdentifyConference() {
		return mUseSubject;
	}


	public int getPixel(int dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		return ((int) (dp * metrics.density));
	}


}