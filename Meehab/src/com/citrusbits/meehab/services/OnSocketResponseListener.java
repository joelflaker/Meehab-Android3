/**
 * 
 */
package com.citrusbits.meehab.services;

/**
 * @author Qamar
 *
 */
public interface OnSocketResponseListener {
	public void onSocketResponseSuccess(String event, Object obj);
	public void onSocketResponseFailure(String message);
}
