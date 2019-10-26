package com.sample.git.parser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@Profile({"test"})
public class TestConfiguration {

	@Bean
	public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
		return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
}
