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

import com.citrusbits.meehab.model.MeetingModel;
import com.google.gson.annotations.Expose;

public class MeetingResponse {

@Expose
private List<MeetingModel> getAllMeetings = new ArrayList<MeetingModel>();
@Expose
private String message;
@Expose
private Boolean type;

/**
* 
* @return
* The getAllMeetings
*/
public List<MeetingModel> getGetAllMeetings() {
return getAllMeetings;
}

/**
* 
* @param getAllMeetings
* The getAllMeetings
*/
public void setGetAllMeetings(List<MeetingModel> getAllMeetings) {
this.getAllMeetings = getAllMeetings;
}

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

}