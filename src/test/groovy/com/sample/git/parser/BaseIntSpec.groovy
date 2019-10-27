package com.sample.git.parser

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo

@SpringBootTest
@ActiveProfiles(["test"])
@ContextConfiguration(loader = SpringBootContextLoader)
class BaseIntSpec extends BaseSpec {

	@Autowired
	private RestTemplate gitRestTemplate

	protected MockRestServiceServer mockServer

	@Value('${git.api.baseUri}')
	protected String baseUrl

	def setup() {
		mockServer = MockRestServiceServer.createServer(gitRestTemplate)
	}

	protected void mockRequestResponse(String path, HttpMethod requestMethod, HttpStatus responseStatus, String responseFileName) {
		URL base = new URL(baseUrl)
		URL url = new URL(base, path)

		mockServer.expect(ExpectedCount.manyTimes(), requestTo(url.toString()))
				.andExpect(method(requestMethod))
				.andRespond(MockRestResponseCreators
						.withStatus(responseStatus)
						.contentType(MediaType.APPLICATION_JSON)
						.body(this.class.getResourceAsStream(responseFileName).bytes)
				)
	}
}
