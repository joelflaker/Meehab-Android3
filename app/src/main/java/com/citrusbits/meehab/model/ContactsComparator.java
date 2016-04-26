package com.citrusbits.meehab.model;

import java.util.Comparator;

public class ContactsComparator implements Comparator<Contact> {

	@Override
	public int compare(Contact e1, Contact e2) {
		if (e1.getName().compareTo(e2.getName()) > 0) {
			return 1;
		} else {
			return -1;
		}
	}

//	@Override
//	public int compare(Contact e1, Contact e2) {
//		if (e1.getUomeUser().equals("1") || e2.getUomeUser().equals("1")) {
//			return 1;
//		} else {
//			return -1;
//		}
//	}
}
