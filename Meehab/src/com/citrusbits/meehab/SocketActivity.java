package com.citrusbits.meehab;

import org.json.JSONObject;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.services.SocketService;
import com.citrusbits.meehab.services.SocketService.SocketServiceConnectionBinder;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.instabug.wrapper.support.activity.InstabugFragmentActivity;


public abstract class SocketActivity extends InstabugFragmentActivity {

	protected static final int REQUEST_ANNOUNCE_PGP = 0x0101;
	protected static final int REQUEST_INVITE_TO_CONVERSATION = 0x0102;

	public SocketService socketService;
	public boolean socketConnectionServiceBound = false;
	protected boolean registeredListeners = false;

	protected boolean mUseSubject = true;

	private DisplayMetrics metrics;
	protected boolean mUsingEnterKey = false;

//	private long mLastUiRefresh = 0;
//	private Handler mRefreshUiHandler = new Handler();
//	private Runnable mRefreshUiRunnable = new Runnable() {
//		@Override
//		public void run() {
//			mLastUiRefresh = SystemClock.elapsedRealtime();
//			refreshUiReal();
//		}
//	};
//
//
//	protected void refreshUi() {
//		final long diff = SystemClock.elapsedRealtime() - mLastUiRefresh;
//		if (diff > Consts.REFRESH_UI_INTERVAL) {
//			mRefreshUiHandler.removeCallbacks(mRefreshUiRunnable);
//			runOnUiThread(mRefreshUiRunnable);
//		} else {
//			final long next = Consts.REFRESH_UI_INTERVAL - diff;
//			mRefreshUiHandler.removeCallbacks(mRefreshUiRunnable);
//			mRefreshUiHandler.postDelayed(mRefreshUiRunnable,next);
//		}
//	}
//
//	protected void refreshUiReal() {
//
//	};

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
	protected void onStart() {
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

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	protected boolean shouldRegisterListeners() {
		if  (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return !isDestroyed() && !isFinishing();
		} else {
			return !isFinishing();
		}
	}

	public void connectToBackend() {
		Intent intent = new Intent(this, SocketService.class);
		intent.setAction("ui");
		startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (socketConnectionServiceBound) {
			if (registeredListeners) {
				this.unregisterListeners();
				this.registeredListeners = false;
			}
			unbindService(mConnection);
			socketConnectionServiceBound = false;
		}
	}

	protected void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		View focus = getCurrentFocus();

		if (focus != null) {

			inputManager.hideSoftInputFromWindow(focus.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	void onBackendConnected(){
		Log.e("Parent OnBackend called", "Yes");
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
	
	public void emitEvent(String event, JSONObject params){
		
	}
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				break;
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		metrics = getResources().getDisplayMetrics();
	}

	protected SharedPreferences getPreferences() {
		return getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
	}

	public boolean useSubjectToIdentifyConference() {
		return mUseSubject;
	}


	public int getPixel(int dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		return ((int) (dp * metrics.density));
	}

	public boolean copyTextToClipboard(String text, int labelResId) {
		ClipboardManager mClipBoardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		String label = getResources().getString(labelResId);
		if (mClipBoardManager != null) {
			ClipData mClipData = ClipData.newPlainText(label, text);
			mClipBoardManager.setPrimaryClip(mClipData);
			return true;
		}
		return false;
	}

	protected String getShareableUri() {
		return null;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}