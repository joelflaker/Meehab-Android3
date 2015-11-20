package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class SendReceiveMessageResponse implements Serializable {

	private String message;
	
	private boolean type;
	
	@SerializedName("sendReciveMessages")
	@Expose
	private ChatModel chatMessage;

	public ChatModel getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(ChatModel chatMessage) {
		this.chatMessage = chatMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

}
