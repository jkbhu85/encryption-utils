package com.jk.encryptionutils;

@SuppressWarnings("serial")
public class AppContextException extends RuntimeException {

	private final String context;

	public AppContextException(String context, Throwable cause) {
		super(cause);
		this.context = context;
	}

	@Override
	public String getMessage() {
		return context + ": " + (super.getMessage() == null ? "Unknown error occurred." : super.getMessage());
	}

}
