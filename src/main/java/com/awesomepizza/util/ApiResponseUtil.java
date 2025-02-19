package com.awesomepizza.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

	public static <T> ResponseEntity<T> success(T data) {
		return ResponseEntity.ok(data);
	}

	public static <T> ResponseEntity<T> unauthorized(T data) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	public static ResponseEntity<String> error(String message, HttpStatus status) {
		return ResponseEntity.status(status).body(message);
	}

}
