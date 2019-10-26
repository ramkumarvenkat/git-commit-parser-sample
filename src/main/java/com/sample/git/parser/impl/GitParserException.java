package com.sample.git.parser.impl;

public class GitParserException extends Exception {

	public GitParserException(String message) {
		super(message);
	}

	public GitParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
