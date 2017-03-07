package com.citrusbits.meehab.webservices;

import android.content.Context;

import com.citrusbits.meehab.pojo.ContactSyncResponse;

public class ContactSyncService extends BaseDataSource {
	private ContactSyncResponse response;
	private Context context;
	
	public ContactSyncResponse getResponse() {
		return response;
	}

	public void SyncContacts(Context context, String uome_user_id, String uome_contact_array, String uome_names_array,
			String uome_token) {

		this.context = context;
		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("uome_user_id", uome_user_id);
//		params.put("uome_contact_array", uome_contact_array);
//		params.put("uome_names_array", uome_names_array);
//		params.put("uome_token", uome_token);
//		
//		RequestQueue requestQueue = MeehabApp.getInstance().getRequestQueue();
//
//		GsonRequest<ContactSyncResponse> request = new GsonRequest<ContactSyncResponse>(
//				com.android.volley.Request.Method.POST, context.getResources().getString(
//						R.string.url) + "methodName=uome_contacts_sync", ContactSyncResponse.class, params,
//				successListener(), errorListener());
//		requestQueue.add(request);
//		request.setRetryPolicy(new DefaultRetryPolicy(50000, 
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
	
//	private Response.Listener<ContactSyncResponse> successListener() {
//		return new Response.Listener<ContactSyncResponse>() {
//			@Override
//			public void onResponse(ContactSyncResponse response) {
//				try {
//					ContactSyncService.this.response = response;
//				} catch (Exception e) {
//					ContactSyncService.this.response = new ContactSyncResponse();
//					ContactSyncService.this.response.setStatus("0");
//					ContactSyncService.this.response.setMessage(context.getResources().getString(R.string.time_out));
//				}
//				triggerObservers();
//			};
//		};
//	}
//
//	private Response.ErrorListener errorListener() {
//		return new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				ContactSyncService.this.response = new ContactSyncResponse();
//				ContactSyncService.this.response.setStatus("0");
//				ContactSyncService.this.response.setMessage(context.getResources().getString(R.string.time_out));
//				triggerObservers();
//			}
//		};
//	}
}