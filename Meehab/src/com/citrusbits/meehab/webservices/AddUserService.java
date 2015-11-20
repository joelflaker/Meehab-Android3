package com.citrusbits.meehab.webservices;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.app.GsonRequest;
import com.citrusbits.meehab.pojo.AddUserResponse;

public class AddUserService extends BaseDataSource {
	private AddUserResponse response;

	public AddUserResponse getResponse() {
		return response;
	}

	public void AddUser(Context context, String username, String email,
			String password, String lat, String lng, String qbid, String token) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("email", email);
		params.put("password", password);
		params.put("lat", lat);
		params.put("lng", lng);
		params.put("qbid", qbid);
		params.put("token", token);
		
		RequestQueue requestQueue = App.getInstance().getRequestQueue();

		GsonRequest<AddUserResponse> request = new GsonRequest<AddUserResponse>(
				com.android.volley.Request.Method.POST, context.getResources().getString(
						R.string.url) + "adduser", AddUserResponse.class, params,
				successListener(), errorListener());
		requestQueue.add(request);
	}
	
	public void CheckEmail(Context context, String email) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		
		RequestQueue requestQueue = App.getInstance().getRequestQueue();

		GsonRequest<AddUserResponse> request = new GsonRequest<AddUserResponse>(
				com.android.volley.Request.Method.POST, context.getResources().getString(
						R.string.url) + "checkemail", AddUserResponse.class, params,
				successListener(), errorListener());
		requestQueue.add(request);
	}
	
	public void CheckUsername(Context context, String username) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		
		RequestQueue requestQueue = App.getInstance().getRequestQueue();

		GsonRequest<AddUserResponse> request = new GsonRequest<AddUserResponse>(
				com.android.volley.Request.Method.POST, context.getResources().getString(
						R.string.url) + "checkusername", AddUserResponse.class, params,
				successListener(), errorListener());
		requestQueue.add(request);
	}

	private Response.Listener<AddUserResponse> successListener() {
		return new Response.Listener<AddUserResponse>() {
			@Override
			public void onResponse(AddUserResponse response) {
				try {
					AddUserService.this.response = response;
				} catch (Exception e) {
					AddUserService.this.response = new AddUserResponse();
//					AddUserService.this.response.setStatus("0");
					
					AddUserService.this.response.setMessage(e.getMessage());
				}
				triggerObservers();
			};
		};
	}

	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				AddUserService.this.response = new AddUserResponse();
//				AddUserService.this.response.setStatus("0");
				AddUserService.this.response.setMessage(error.toString());
				triggerObservers();
			}
		};
	}
}