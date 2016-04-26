package com.citrusbits.meehab.contacts;

import org.json.JSONArray;

import android.content.Context;
import android.os.AsyncTask;

public class GetContactTask extends AsyncTask<Void, Void, JSONArray> {

	private Context context;
	private ContactsListener listener;

	public GetContactTask(Context context) {
		this.context = context;
	}

	public GetContactTask setContactsListener(ContactsListener listener) {
		
		this.listener = listener;
		
		return this;
	}

	@Override
	protected JSONArray doInBackground(Void... params) {
		return new PhoneContacts(context).ReadPhoneContacts();
	}

	@Override
	protected void onPostExecute(JSONArray jsonArray) {
		super.onPostExecute(jsonArray);
		if (listener != null) {
			listener.onFetchContacts(jsonArray);
		}
	}

	public interface ContactsListener {
		void onFetchContacts(JSONArray jsonArray);
	}

}
