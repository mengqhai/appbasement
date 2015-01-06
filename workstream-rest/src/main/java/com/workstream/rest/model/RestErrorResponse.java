package com.workstream.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestErrorResponse {
	private int errorCode;
	private String message;
	private String developerMessage;
	private String moreInfoUrl;

	public RestErrorResponse() {
	}

	public RestErrorResponse(int code, String message) {
		this.errorCode = code;
		this.message = message;
	}

	public RestErrorResponse(int code, String message, String developerMessage,
			String moreInfoUrl) {
		super();
		this.errorCode = code;
		this.message = message;
		this.developerMessage = developerMessage;
		this.moreInfoUrl = moreInfoUrl;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int code) {
		this.errorCode = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public String getMoreInfoUrl() {
		return moreInfoUrl;
	}

	public void setMoreInfoUrl(String moreInfoUrl) {
		this.moreInfoUrl = moreInfoUrl;
	}

}
