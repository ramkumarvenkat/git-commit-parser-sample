package com.sample.git.parser.api

import com.sample.git.parser.BaseIntSpec
import com.sample.git.parser.impl.GitParserException
import com.sample.git.parser.impl.gitcli.GitCliClient
import com.sample.git.parser.impl.gitcli.ProcessRunner
import com.sample.git.parser.impl.models.Commit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Stepwise

import java.time.Instant
import java.util.concurrent.CompletableFuture

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Stepwise
class GitParserApiControllerIntSpec extends BaseIntSpec {

	@Autowired
	private MockMvc mockMvc

	@Autowired
	private GitCliClient client

	private ProcessRunner runner

	def setup() {
		runner = Mock(ProcessRunner)
		client.setRunner(runner)
	}

	def "getCommits works with the github api"() {
		given:
			def url = "https://github.com/test/test1.git"
			mockRequestResponse("/repos/test/test1/commits", HttpMethod.GET, HttpStatus.OK, "/impl/gitapi/response.json")

		when:
			def results = mockMvc.perform(get("/api/v1/git/parser/commits?url=" + url)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(this.class.getResourceAsStream("/api/response.json").bytes))

		then:
			results.andExpect(status().isOk())

		and:
			0 * runner.run(_, _, "git", "clone", _, _)
	}

	def "getCommits works when github api fails and fallback to git cli"() {
		given:
			def url = "https://github.com/test/test1.git"

			mockRequestResponse("/repos/test/test1/commits", HttpMethod.GET, HttpStatus.INTERNAL_SERVER_ERROR, "/impl/gitapi/response.json")

			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()

			def logOutputFuture = new CompletableFuture<List<Commit>>()
			def logErrorFuture = new CompletableFuture<List<String>>()

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "Unpacking objects: 100% (98/98), done"))

			logOutputFuture.complete(Arrays.asList(new Commit("sha", "name", "name@gmail.com", Instant.ofEpochMilli(1000), "subject")))
			logErrorFuture.complete(Collections.emptyList())

			def results = mockMvc.perform(get("/api/v1/git/parser/commits?url=" + url)
					.contentType(MediaType.APPLICATION_JSON_VALUE))

		then:
			results.andExpect(status().isOk())

		and:
			1 * runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)
			1 * runner.run(_, _, "git", "log", _, _, _, _) >> new ProcessRunner.ExecutionResult(logOutputFuture, logErrorFuture)
	}

	def "getCommits retries happen when git clone times out"() {
		when:
			def url = "https://github.com/test/test1.git"

			mockRequestResponse("/repos/test/test1/commits", HttpMethod.GET, HttpStatus.INTERNAL_SERVER_ERROR, "/impl/gitapi/response.json")

			def results = mockMvc.perform(get("/api/v1/git/parser/commits?url=" + url)
					.contentType(MediaType.APPLICATION_JSON_VALUE))

		then:
			results.andExpect(status().isInternalServerError())

		and:
			3 * runner.run(_, _, "git", "clone", _, _) >> {
				throw new GitParserException("error")
			}
			0 * runner.run(_, _, "git", "log", _, _, _, _)
	}
}
