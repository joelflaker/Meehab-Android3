/**
 * 
 */
package com.citrusbits.meehab.pojo;

/**
 * @author Xamar
 *
 */
import java.util.ArrayList;
import java.util.List;

import com.citrusbits.meehab.model.MeetingReviewModel;
import com.google.gson.annotations.Expose;

public class MeetingReviewsResponse {

@Expose
private String message;
@Expose
private Boolean type;
@Expose
private List<MeetingReviewModel> getMeetingsReviews = new ArrayList<MeetingReviewModel>();

/**
* 
* @return
* The message
*/
public String getMessage() {
return message;
}

/**
* 
* @param message
* The message
*/
public void setMessage(String message) {
this.message = message;
}

/**
* 
* @return
* The type
*/
public Boolean getType() {
return type;
}

/**
* 
* @param type
* The type
*/
public void setType(Boolean type) {
this.type = type;
}

/**
* 
* @return
* The getMeetingsReviews
*/
public List<MeetingReviewModel> getGetMeetingsReviews() {
return getMeetingsReviews;
}

/**
* 
* @param getMeetingsReviews
* The getMeetingsReviews
*/
public void setGetMeetingsReviews(List<MeetingReviewModel> getMeetingsReviews) {
this.getMeetingsReviews = getMeetingsReviews;
}

}
