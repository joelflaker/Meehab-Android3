package com.citrusbits.meehab.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.citrusbits.meehab.model.Contact;

public class ContactsUtils {

	public static ArrayList<Contact> getContacts(Context context) {
		ArrayList<Contact> allContacts = null;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		if (cursor.moveToFirst()) {
			allContacts = new ArrayList<>();
			do {
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

				if (Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

					while (pCur.moveToNext()) {
						Contact item = new Contact();
						item.setNumber(pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
						item.setName(pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
						//						item.setPicture(pCur
						//								.getString(pCur
						//										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));

						Cursor emailCur = cr
								.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Email.CONTACT_ID
										+ " = ?", new String[] { id },
										null);
						emailCur.moveToFirst();
						if (emailCur.getCount() > 0) {
							item.setEmail(emailCur
									.getString(emailCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
						} else {
							item.setEmail("");
						}
						emailCur.close();

						//remove spaces between number
						item.setNumber(item.getNumber().replace(" ", ""));
						item.setNumber(item.getNumber().replace("-", ""));
						item.setNumber(item.getNumber().replace("(", ""));
						item.setNumber(item.getNumber().replace(")", ""));
						item.setNumber(item.getNumber().substring(Math.max(0, item.getNumber().length() - 10)));
						allContacts.add(item);
						break;
					}
					pCur.close();
				}

			} while (cursor.moveToNext());


			//call the function and filter the array list
			//remove contacts that doesnot have either name or number
			allContacts = filterContactsArray(allContacts);
		}
		
		return allContacts;
	}
	
	public static ArrayList<Contact> filterContactsArray(ArrayList<Contact> contacts) {
		Log.i("size before", String.valueOf(contacts.size()));
		if(contacts.size() > 0) {
			for(int i=0; i<contacts.size(); i++) {
				if(contacts.get(i).getName() == null || contacts.get(i).getNumber() == null) {
					contacts.remove(i);
				}
			}
			Log.i("size after", String.valueOf(contacts.size()));
		}
		
		return contacts;
	}
	
	public static String getJsonNumbers(ArrayList<Contact> contacts) {
		String numbers = "(";
		
		for (int i = 0; i < contacts.size(); i++) {
			// jsonArray.put(contacts.get(i));
			Contact num = contacts.get(i);
			num.setNumber(num.getNumber().replace(" ", ""));
			num.setNumber("'" + num.getNumber() + "'");
			if (numbers.compareTo("(") == 0) {
				numbers = numbers + num.getNumber();
			} else {
				numbers = numbers + "," + num.getNumber();
			}
		}
		
		return numbers;
	}

	public static String getJsonNames(ArrayList<Contact> contacts) {
		String names = "(";
		
		for (int i = 0; i < contacts.size(); i++) {
			// jsonArray.put(contacts.get(i));
			Contact num = contacts.get(i);
//			num.setName(num.getName().replace(" ", ""));
			num.setName("'" + num.getName() + "'");
			if (names.compareTo("(") == 0) {
				names = names + num.getName();
			} else {
				names = names + "," + num.getName();
			}
		}
		
		return names;
	}
	
}
