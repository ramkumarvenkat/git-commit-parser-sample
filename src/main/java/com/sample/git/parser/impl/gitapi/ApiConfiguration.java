package com.sample.git.parser.impl.gitapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
@RequiredArgsConstructor
public class ApiConfiguration {

	@Value("${git.api.baseUri}")
	private String baseUrl;

	@Value("${git.api.connect.timeout.seconds:5}")
	private String connectTimeoutSeconds;

	@Value("${git.api.read.timeout.seconds:30}")
	private String readTimeoutSeconds;

	private final RestTemplateBuilder builder;

	@Bean
	RestTemplate gitApiRestTemplate() {
		RestTemplate restTemplate = builder.rootUri(baseUrl)
				.setConnectTimeout(Duration.of(Long.valueOf(connectTimeoutSeconds), SECONDS))
				.setReadTimeout(Duration.of(Long.valueOf(readTimeoutSeconds), SECONDS))
				.build();
		return restTemplate;
	}
}
