/**
 * 
 */
package com.citrusbits.meehab.constants;

/**
 * @author Qamar
 * 
 */
public class EventParams {

	public static final String EVENT_USER_SIGNUP = "users:signup";
	public static final String EVENT_USER_LOGOUT = "users:logout";
	public static final String EVENT_USER_FORGOT_PASSWORD = "users:forgotPassword";
	public static final String EVENT_USER_UPDATE = "users:updateProfile";
	public static final String EVENT_USER_FACEBOOK = "users:loginFB";
	public static final String EVENT_MEETING_GET_ALL = "meetings:getAllMeetings";
	
	public static final String EVENT_GET_ALL_REHABS = "rehabs:getAllRehabs";
	
	public static final String EVENT_MEETING_GET_REVIEWS = "meetings:getMeetingsReviews";
	public static final String EVENT_MEETING_ADD_REVIEW = "meetings:addMeetingsReview";

	public static final String EVENT_GET_USER_REVIEWS = "meetings:getUserReviews";
	public static final String EVENT_DELETE_USER_REVIEW = "meetings:deleteUserReviews";

	public static final String EVENT_FAVOURITE_LIST = "meetings:getAllFavorites";

	public static final String EVENT_ADD_USER_FAVOURITE = "meetings:addUserFavorite";
	
	public static final String EVENT_ADD_MEETING = "meetings:addMeeting";
	
	public static final String EVENT_ADD_REHAB = "rehabs:add:rehab";
	
	public static final String EVENT_ADD_INSURANCE = "rehabs:update:insurance";

	public static final String METHOD_USER_LOGIN = "users:login";

	public static final String METHOD_CHAT_SEND_RECEIVE = "chat:sendReciveMessages";

	public static final String METHOD_CHAT_PAGINATION = "users:chatPagination";
	
	public static final String METHOD_BLOCK_USER_NOTIFY = "users:userblocked";
	
	public static final String METHOD_DISCONNECT_SOCKET = "disconnect";
	public static final String METHOD_CONNECT_SOCKET = "connect";
	
	
	public static final String METHOD_ERROR_SOCKET = "error";
	public static final String METHOD_RE_CONNECT_SOCKET = "reconnect";
	public static final String METHOD_RECONNECT_ATTEMPT_SOCKET = "reconnectAttempt";
	
	public static final String METHOD_CHECK_IN_USER = "users:friendcheckin";
	

	public static final String METHOD_DELETE_CHAT_MESSAGE = "users:deleteChatMessages";

	public static final String METHOD_GET_CHAT_FRIENDS = "users:getChatFrinds";
	public static final String METHOD_DELETE_CONVERSATION = "users:deleteConversetion";
	
	public static final String METHOD_BLOCK_USER = "users:blockUser";
	
	public static final String METHOD_SYNC_PHONE = "users:syncPhone";
	public static final String METHOD_REPORT_USER = "users:reportUser";
	
	public static final String METHOD_HOME_GROUP_USER = "meetings:homeGroupUser";
	
	public static final String METHOD_RSVP = "users:rsvpMeeting";
	
	
	
	public static final String METHOD_REPORT_MEETING = "meetings:reportMeeting";
	public static final String METHOD_FAVOURITE_USER = "users:favoriteUser";
	public static final String METHOD_ACCESS_TOKEN = "users:accessToken";

	public static final String METHOD_CHECK_INFO = "users:checkInfo";  
	public static final String METHOD_GET_ALL_FRIENDS = "users:getAllFriends";
	public static final String METHOD_RSVP_USERS = "meetings:rsvpUsers";
	
	public static final String METHOD_CHECK_IN_MEETING = "users:chickinMeeting";

	public static final String BASE64_IMAGE_PNG_STRING = "data:image/png;base64,";
	public static final String SIGNUP_USERNAME = "username";
	public static final String SIGNUP_EMAIL = "email";
	public static final String SIGNUP_PASSWORD = "password";
	public static final String SIGNUP_TYPE = "signup_type";
	public static final String SIGNUP_SOCIAL_ID = "socail_id";

	public static enum SIGNUP_TYPE_VALUE {
		account, facebook
	};

	public static enum UPDATE_INTRESTED_IN {
		Dating, Fellowshipping, Both
	};

	public static final String UPDATE_XYZ = "xyz";

}
