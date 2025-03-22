package com.softwaretunnel.javaapp;

public enum ExceptionMessage {
	REST_CALL_FAILED("Rest Service Call failed");

	public final String message;

	ExceptionMessage(String message) {
		this.message = message;

	}
}
