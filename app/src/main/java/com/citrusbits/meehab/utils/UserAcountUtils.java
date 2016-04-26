/**
 * 
 */
package com.citrusbits.meehab.utils;

import android.text.TextUtils;

import com.citrusbits.meehab.model.UserAccount;

/**
 * @author Qamar
 *
 */
public class UserAcountUtils {

public static boolean setDefaultIfNull(UserAccount user){
		
		if(user == null) return false;
		String unspecified = "Unspecified";
		
		if(TextUtils.isEmpty(user.getAboutStory())){
			user.setAboutStory(unspecified);
		}
		if(TextUtils.isEmpty(user.getWeight())){
			user.setWeight(unspecified);
		}
		if(TextUtils.isEmpty(user.getIntrestedIn())){
			user.setIntrestedIn(unspecified);
		}
		if(TextUtils.isEmpty(user.getSexualOrientation())){
			user.setSexualOrientation(unspecified);
		}
		if(TextUtils.isEmpty(user.getHeight())){
			user.setHeight(unspecified);
		}
		if(TextUtils.isEmpty(user.getWillingSponsor())){
			user.setWillingSponsor(unspecified);
		}
		if(TextUtils.isEmpty(user.getGender())){
			user.setGender(unspecified);
		}
		if(TextUtils.isEmpty(user.getMaritalStatus())){
			user.setMaritalStatus(unspecified);
		}
		if(TextUtils.isEmpty(user.getHaveKids())){
			user.setHaveKids(unspecified);
		}
		if(TextUtils.isEmpty(user.getDateOfBirth())){
			user.setDateOfBirth(unspecified);
		}
		if(TextUtils.isEmpty(user.getSoberSence())){
			user.setSoberSence(unspecified);
		}
		
		if(TextUtils.isEmpty(user.getAccupation())){
			user.setAccupation(unspecified);
		}
		if(TextUtils.isEmpty(user.getEthnicity())){
			user.setEthnicity(unspecified);
		}
		
		return true;
	}
}
