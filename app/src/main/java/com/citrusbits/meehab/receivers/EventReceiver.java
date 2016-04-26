package com.citrusbits.meehab.receivers;

import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.services.SocketService;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mIntentForService = new Intent(context,
				SocketService.class);
		if (intent.getAction() != null) {
			mIntentForService.setAction(intent.getAction());
		} else {
			mIntentForService.setAction("other");
		}

		if (intent.getAction().equals("ui") || new UserDatasource(context).hasAccounts()) {
			if(NetworkUtil.getConnectivityStatus(context) != 0){
				context.startService(mIntentForService);
			}
		}
	}

}
