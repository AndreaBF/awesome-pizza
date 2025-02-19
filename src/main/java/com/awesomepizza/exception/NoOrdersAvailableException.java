package com.awesomepizza.exception;

public class NoOrdersAvailableException extends RuntimeException {

	private static final long serialVersionUID = -4402777212939967098L;

	public NoOrdersAvailableException(String errorMessage) {
		super(errorMessage);
	}

}
