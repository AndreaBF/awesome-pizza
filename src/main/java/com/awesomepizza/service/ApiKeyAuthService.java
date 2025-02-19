package com.awesomepizza.service;

import org.springframework.stereotype.Service;

import com.awesomepizza.config.ApiKeyAuthConfig;

@Service
public class ApiKeyAuthService {
	private final ApiKeyAuthConfig apiKeyAuthConfig;

	public ApiKeyAuthService(ApiKeyAuthConfig apiKeyAuthConfig) {
		this.apiKeyAuthConfig = apiKeyAuthConfig;
	}

	public boolean isValidApiKey(String apiKey) {
		return apiKey != null && apiKey.equals(apiKeyAuthConfig.getKey());
	}
}