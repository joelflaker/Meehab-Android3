package com.citrusbits.meehab.contacts;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.services.DeviceContact;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.widget.Toast;

public class PhoneContacts {
	Context cntx;

	public PhoneContacts(Context context) {
		this.cntx = context;
	}

	public JSONArray ReadPhoneContacts() // This Context parameter
											// is
	// nothing but your Activity
	// class's Context
	{

		JSONArray phoneBook = new JSONArray();

		Cursor cursor = cntx.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		if( cursor == null){
			return phoneBook;
		}

		final Integer contactsCount = cursor.getCount(); // get how many
															// contacts you
		// have in your contacts
		// list

		int hasPhoneCount = 0;
		int notHasPhoneCount = 0;

		if (contactsCount > 0) {
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String contactName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// the below cursor will give you details for multiple
					// contacts
					Cursor pCursor = cntx.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					// continue till this cursor reaches to all phone numbers
					// which are associated with a contact in the contact list
					hasPhoneCount++;

					while (pCursor.moveToNext()) {
						int phoneType = pCursor
								.getInt(pCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						// String isStarred =
						// pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
						String phoneNo = pCursor
								.getString(pCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						// you will get all phone numbers according to it's type
						// as below switch case.
						// Logs.e will print the phone number along with the
						// name in DDMS. you can use these details where ever
						// you want.
						JSONObject phone = new JSONObject();
						try {

							phone.put("phone", phoneNo);
							phone.put("name", contactName);

							phoneBook.put(phone);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					pCursor.close();
				} else {
					notHasPhoneCount++;
				}
			}
			cursor.close();
		}

		Log.e("Has phone count ", String.valueOf(hasPhoneCount));
		Log.e("No has phone count ", String.valueOf(notHasPhoneCount));

		return phoneBook;
	}

	public List<DeviceContact> getPhoneContacts() // This Context parameter
	// is
	// nothing but your Activity
	// class's Context
	{
		Cursor cursor = cntx.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		final Integer contactsCount = cursor.getCount(); // get how many
		// contacts you
		// have in your contacts
		// list

		List<DeviceContact> contacts = new ArrayList<DeviceContact>();

		int hasPhoneCount = 0;
		int notHasPhoneCount = 0;

		if (contactsCount > 0) {
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String contactName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// the below cursor will give you details for multiple
					// contacts
					Cursor pCursor = cntx.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					// continue till this cursor reaches to all phone numbers
					// which are associated with a contact in the contact list
					hasPhoneCount++;

					while (pCursor.moveToNext()) {
						int phoneType = pCursor
								.getInt(pCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						// String isStarred =
						// pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));

						String phoneNo = pCursor
								.getString(pCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						phoneNo=phoneNo.replace(" ", "");
						

						DeviceContact contact = new DeviceContact();
						contact.setContactId(Integer.parseInt(id));
						contact.setContactName(contactName);
						contact.setPhoneNumber(phoneNo);
						contacts.add(contact);

					}

					pCursor.close();
				} else {
					notHasPhoneCount++;
				}
			}
			cursor.close();
		}

		Log.e("Has phone count ", String.valueOf(hasPhoneCount));
		Log.e("No has phone count ", String.valueOf(notHasPhoneCount));

		return contacts;
	}

}
